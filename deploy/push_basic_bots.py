import os
import docker
import json

passwords = []

if (not os.path.exists("passwords.json")):
    print('Run generate_passwords.py first')
    exit(-1)
else:
    passwords = json.load(open("./passwords.json", "r"))


teams = ["africa", "asia", "canada", "europe", "latina", "oceania", "usa"]

client = docker.from_env()
for x in range(len(teams)):
    print(f"Setting up for team {teams[x]}")
    print(client.images.build(path="./docker_game_manager/docker_images/base_player_image",
          dockerfile="Dockerfile", tag=f"{teams[x]}.koth.icc.cybersecnatlab.it/bot/bot:latest"))
    os.system(
        f"docker login https://{teams[x]}.koth.icc.cybersecnatlab.it -u {teams[x]} -p {passwords[x]}")
    os.system(
        f"docker push {teams[x]}.koth.icc.cybersecnatlab.it/bot/bot:latest")

print(client.images.build(path="./docker_game_manager/docker_images/backend_image",
      dockerfile="Dockerfile", tag=f"kothbackend:latest"))
