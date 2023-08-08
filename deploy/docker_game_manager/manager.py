import json
import docker
import os
import random
import time
import verboselogs
import coloredlogs
import logging
import shutil
import threading
import copy

global_scoreboard_lock = threading.Lock()

teams = ["africa", "asia", "canada", "europe", "latina", "oceania", "usa"]
passwords = []
if (not os.path.exists("../passwords.json")):
    print('Run generate_passwords.py first')
    exit(-1)
else:
    passwords = json.load(open("../passwords.json", "r"))

if os.geteuid() != 0:
    print("Running this script not as root may break it as it writes into a directory mounted by a docker container")
    char = input("Are you sure you want to continue? (y/N)")
    if (char != 'y'):
        exit(-2)

# TODO: Update before the start of the competition
GAME_START = 1690448593  # timestamp of game start
ROUND_DELTA = 60*6  # 6 minutes between rounds
GAME_LENGTH = 60*60*8  # 60*60*8 # 8 hours
GAME_END = GAME_START+GAME_LENGTH
GAME_FREEZE_DELTA = 60*60*6  # freeze after 6 hours
GAME_FREEZE_ROUND = GAME_FREEZE_DELTA//ROUND_DELTA


client = docker.from_env()
client.images.build(path="./docker_images/backend_image",
                    dockerfile="Dockerfile", tag=f"kothbackend:latest")
logger = verboselogs.VerboseLogger('KOTH Game manager')
logger.addHandler(logging.StreamHandler())
logger.setLevel(logging.DEBUG)
coloredlogs.install(level='DEBUG', logger=logger)
next_round = 0

if (not os.path.exists("./results")):
    os.makedirs("./results")

if (not os.path.exists("./results/outputs")):
    os.makedirs("./results/outputs")

if (not os.path.exists("./results/images")):
    os.makedirs("./results/images")

if (not os.path.exists("./results/images/latest_ids.json")):
    f = open("./results/images/latest_ids.json", "w")
    f.write("{}")
    f.close()

# Complete rounds is for the frontend to know which games are ready
if (not os.path.exists("./results/complete_rounds.json")):
    f = open("./results/complete_rounds.json", "w")
    f.write("[]")
    f.close()
else:
    data = json.load(open("./results/complete_rounds.json", "r"))
    if (len(data) > 0):
        # If it already exists, start from next game not complete to avoid overwriting!
        next_round = max(data)+1

if (not os.path.exists("./results/current_scoreboard.json")):  # Current scoreboard is the unfrozen one
    f = open("./results/current_scoreboard.json", "w")
    data = {-1: {_: 0 for _ in teams}}
    f.write(json.dumps(data))
    f.close()

if (not os.path.exists("./results/user_scoreboard.json")):  # User scoreboard is the frozen one
    f = open("./results/user_scoreboard.json", "w")
    data = {-1: {_: 0 for _ in teams}}
    f.write(json.dumps(data))
    f.close()


def init_random_networks(round):
    networks = []
    for x in range(len(teams)):
        networks.append(client.networks.create(
            f"koth-round{round}-{teams[x]}", driver="bridge", internal=True))
    return networks


def update_images(round):  # Update the images from the registries
    os.makedirs(f"./results/images/{round}")
    latest_ids_file = open("./results/images/latest_ids.json", "r")
    latest_ids = json.load(latest_ids_file)
    latest_ids_file.close()
    for x in range(len(teams)):
        image = client.images.pull(f"{teams[x]}.koth.icc.cybersecnatlab.it/bot/bot:latest", auth_config={
                                   "username": teams[x], "password": passwords[x]})
        id = image.id
        if (teams[x] not in latest_ids or latest_ids[teams[x]] != id):
            latest_ids[teams[x]] = id
            f = open(f'./results/images/{round}/{teams[x]}.tar', 'wb')
            for chunk in image.save():
                f.write(chunk)
            f.close()
        print(image)
    latest_ids_file = open("./results/images/latest_ids.json", "w")
    json.dump(latest_ids, latest_ids_file)
    latest_ids_file.close()


def delete_networks(networks):
    for network in networks:
        network.remove()


def handle_round(round):
    round_logger = verboselogs.VerboseLogger(
        f'KOTH Game manager - Round {round}')
    round_logger.addHandler(logging.StreamHandler())
    round_logger.setLevel(logging.DEBUG)
    coloredlogs.install(level='DEBUG', logger=round_logger)
    round_logger.info("Starting new round")
    round_logger.info("Preparing folders")

    if (os.path.exists(f"./results/outputs/{round}")):
        shutil.rmtree(f"./results/outputs/{round}")

    os.mkdir(f"./results/outputs/{round}")
    os.mkdir(f"./results/outputs/{round}/backend")

    round_logger.info("Creating networks")
    networks = init_random_networks(round)  # TODO: check network size docker
    round_logger.info("Updating images")
    update_images(round)

    round_logger.info("Creating player containers")

    player_containers = []

    for x in range(len(teams)):  # start proper container for each player
        player_containers.append(client.containers.run(f"{teams[x]}.koth.icc.cybersecnatlab.it/bot/bot:latest", name=f"round{round}-bot-{teams[x]}",
                                 hostname=teams[x], network=networks[x].name, mem_limit="2g", nano_cpus=2000000000, detach=True))

    round_logger.info("Creating backend container")

    backend = client.containers.run("kothbackend:latest", name=f"round{round}-kothbackend",  volumes=[
                                    os.getcwd()+f'/results/outputs/{round}:/output', os.getcwd()+f'/config.json:/config.json'], detach=True)
    for network in networks:
        network.connect(backend)

    round_logger.info("Waiting for game to finish")

    backend.wait()

    round_logger.success("Game succesfully finished")
    round_logger.info("Saving logs for backend")
    f = open(f"./results/outputs/{round}/backend/container.logs", "w")
    f.write(backend.logs().decode())
    f.close()
    backend.remove()
    for x in range(len(teams)):
        try:
            round_logger.info(f"Killing team {teams[x]}")
            player_containers[x].kill()  # kill them all!
        except Exception as ex:
            round_logger.exception(ex)
        round_logger.info(f"Saving logs for team {teams[x]}")
        # write logs for each bot
        f = open(f"./results/outputs/{round}/{teams[x]}/container.logs", "w")
        f.write(player_containers[x].logs(tail=1000).decode())
        f.close()
        player_containers[x].remove()

    round_logger.info("Parsing scoreboard")
    failed = False
    scoreboard = []
    try:
        # read scoreboard for round
        f = open(f"./results/outputs/{round}/scoreboard.json", "r")
        scoreboard = json.load(f)
        f.close()
    except Exception as e:
        failed = True
        round_logger.exception(e)
    if (not failed):

        global_scoreboard_lock.acquire()
        f = open(f"./results/current_scoreboard.json", "r")
        current_scoreboard_full = json.load(f)
        f.close()
        last_valid_round = str(
            max([int(x) for x in current_scoreboard_full.keys()]))
        current_scoreboard = copy.deepcopy(
            current_scoreboard_full[last_valid_round])

        for summoner in scoreboard:
            current_scoreboard[summoner["name"]] += summoner["score"]
        current_scoreboard_full[round] = current_scoreboard

        f = open(f"./results/current_scoreboard.json", "w")
        json.dump(current_scoreboard_full, f)
        f.close()

        if (round < GAME_FREEZE_ROUND):
            f = open(f"./results/user_scoreboard.json", "r")
            current_scoreboard_full = json.load(f)
            f.close()
            last_valid_round = str(
                max([int(x) for x in current_scoreboard_full.keys()]))
            current_scoreboard = copy.deepcopy(
                current_scoreboard_full[last_valid_round])

            for summoner in scoreboard:
                current_scoreboard[summoner["name"]] += summoner["score"]
            current_scoreboard_full[round] = current_scoreboard

            f = open(f"./results/user_scoreboard.json", "w")
            json.dump(current_scoreboard_full, f)
            f.close()

        # let the frontend know we have new rounds
        f = open(f"./results/complete_rounds.json", "r")
        data = json.loads(f.read())
        data.append(round)
        f.close()
        f = open(f"./results/complete_rounds.json", "w")
        f.write(json.dumps(data))
        f.close()

        global_scoreboard_lock.release()

    delete_networks(networks)


logger.info("Waiting for competition to start")
while (int(time.time()) < GAME_START):
    time.sleep(1)
logger.info("Starting competition")

while (int(time.time()) < GAME_END):
    t = threading.Thread(target=handle_round, args=(next_round,))
    t.start()
    time.sleep(20)
    next_round += 1
    while (int(time.time()) % ROUND_DELTA > 10):
        time.sleep(5)
