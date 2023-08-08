# KOTH deploy

This folder contains everything needed to fully deploy a server hosting a king of the hill competition.

## Folder contents

 - `auth/`: passwords used by the docker registries
 - `docker_game_manager/`: main docker images for the backend and basic bot, and the python game manager
 - `traefik/`: the traefik configuration and compose
 - `webservice/`: the frontend written in node
 - `docker-compose.yml`: the docker-compose to start the registries and frontend
 - `generate_passwords.py`: utility script to generate new passwords
 - `passwords.json`: the passwords.json file used by all scripts
 - `push_basic_bots.py`: utility script to push basic bots to all registries

## Deploy requirements

In order to deploy the registries you'll need a domain name. This is required for the registries to work properly.

If you don't have access to a domain you can still run the infrastructure but each player, in order to push the bots, should properly add the address of the registry to the docker insecure registries. The deploy steps will inform you when to follow the additional instructions in README_NO_HTTPS.md.

## Deploy steps

### Install dependencies

You should have installed docker, docker-compose, apache2-utils, python3 and pip.

### Delete previous games data

Delete the following files or directories:
 - `passwords.json`
 - `auth/*`
 - `docker_game_manager/results/*`: do not delete the results directory

### Prepare list of teams

You should prepare a list of playing teams. This cannot be edited during the competition without several changes to the infrastructure.
The list of teams should be updated in the following files:
 - `generate_passwords.py`: line 17
 - `push_basic_bots.py`: line 14
 - `docker_game_manager/manager.py`: line 15
 - `webservice/backend/index.js`: line 22 to 29. If you want to be able to log into the web interface as `backend` to view the manager logs for each game add an optional `backend` user. Note that it specifically needs to be named `backend`.

Inside the `docker-compose.yml` you should setup a registry configuration for each player. Copy the `registry_africa` example service, delete all the `registry_XXX` services, and create a new service for each player.
For each service be sure to correctly configure:
 - service name, it should be `registry_playerName`
 - REGISTRY_HTTP_HOST, it should be playerName.yourdomain.com
 - labels, you should setup correctly and replace all instances of the example player `africa` with the correct player assigned to that registry
 - volumes, this is especially important for authentication
 - hostname

### Setup correctly all scripts with your domain

You should update and replace `koth.icc.cybersecnatlab.it` with your domain in all the following files:

 - `docker-compose.yml`, several places in all services
 - `push_basic_bots.py`, lines 20, 22, and 24
 - `docker_game_manager/manager.py`, lines 99, and 144
 - `../dist/base_bot/push.sh`, lines 11, 14, and 15

You should setup the domain correctly to point at the IP address of the server running the infrastructure.
Specifically, all domains for the registries (`playerName.yourdomain.com`) and `yourdomain.com` should point correctly.

If you want to run without HTTPS follow the instructions in README_NO_HTTPS.md now.

### Generate new passwords

Run `generate_passwords.py`. It should generate the `passwords.json` file and, inside auth, one directory for each of your players with a `registry.password` file inside.
Copy the `passwords.json` content to `webservice/backend/index.js`, substituting the values on lines 10-18. If you previously added a `backend` user, add one last password for it.
Make sure each user in the `users` variable has assigned the correct password index.

### Starting the infrastructure

Navigate to the traefik directory and launch the docker-compose file with the detach flag.
Verify it is up and running without errors.
Navigate back to this directory and launch the docker-compose file with the detach flag.
You should now be able to navigate to `yourdomain.com` and see the frontend. Make sure you can correctly login as all players. You may find the scoreboard and game list not showing up and few errors on some API calls, this is normal and temporary.

You should also be able to use the `docker login` command to log into any of the players registries.

### Pushing basic bots

Run `push_basic_bots.py` to push the basic bots for each player. The process can be long depending on the number of players you have. For each player you should see a docker push in progress.

### Configure game manager

Edit `docker_game_manager/manager.py`. Set up the following configuration parameters:
 - GAME_START: on line 30, set this up with the timestamp when the game should start.
 - ROUND_DELTA: on line 31, set this up with the amount of seconds between rounds. Note that this needs to be higher than 30 or it may not be respected.
 - GAME_LENGTH: on line 32, set this up with the amount of seconds the whole infrastructure should be running for. For example, if your competition should run for 9 hours set this to 9*60*60.
 - GAME_FREEZE_DELTA: on line 34, set this up with the amount of seconds after which the scoreboard should freeze. Note that new games will still be visible to the players. If you don't need it simply set it longer than the total competition run time (GAME_LENGTH)

Edit `docker_game_manager/config.json`. Set up the following parameters:
 - PLAYER_NAMES: set this up with the player names
 - PLAYER_CONNECTION_URIS: set this up having the same length as PLAYER_NAMES. If you changed the basic bot to listen on a different port you should set the port here correctly. Otherwhise leave port 5000 for each player.
 - FINAL_SCOREBOARD_SCORES: set this up with the points granted for each scoreboard position. Make sure it is at least as long as PLAYER_NAMES or the backend will crash.

You may update other values in `docker_game_manager/config.json`. You can find some warnings and potential side-effects in the section "config.json settings".

If you are using a very short delay between games (ROUND_DELTA<60s), you should expand the docker IP pool on your server.
Refer to [this](https://stackoverflow.com/questions/43720339/docker-error-could-not-find-an-available-non-overlapping-ipv4-address-pool-am) stackoverflow post for the issue you may encounter, specifically [this](https://stackoverflow.com/a/62176334) answer.


### Launch game

You can now run `docker_game_manager/manager.py` into a tmux. It should build the backend image and wait for the game to start.
At each game it will create one new temporary docker network per player, update the local images, and start a new game.


## Results directory

Inside the `docker_game_manager/results/` directory you can find several files, some of which are mounted and served by the frontend.
Here is a comprehensive list of those:
 - `current_scoreboard.json`: a scoreboard file containing the scores at each round. Starting at -1 before the competition, at each round a new key is added. This is the unfreezed version of the scoreboard, not served to the frontend.
 - `user_scoreboard.json`: a scoreboard file identical to `current_scoreboard.json`, but frozen by the manager and not updated after the freeze.
 - `complete_rounds.json`: a list of complete rounds for the frontend to know.
 - `images/`: a directory, storing the saved docker images uploaded by your players. A new image is saved only when changed and not every round. Note that this folder can grow really big.
 - `outputs/`: a directory, storing the game results. For each round a new directory with the round id is created in this folder, containing a `map.json` file with the replay, a `scoreboard.json` file with the final scores, and several directories for the outputs of containers and minions.


## config.json settings

You can override any value set in Config.java (`../backend/src/main/java/it/cybersecnatlab/koth23/Config.java`) with any other value by setting it into `config.json`.

Here is a list of settings you may want to think double before changing in production:
 - SHOULD_SHRINK_MAP: if set to false there is no guarantee the game will end as the fog will never close in. This is intended mainly for testing.
 - OUTPUT_INFO_DIRECTORY: if you change this value in production games may not be saved, scoreboard not updated, and the frontend will probably break too.
 - MAX_WAIT_FOR_MOVE_IN_MS: changing this value may cause games to get extremely long or, if lowered, it may cause players not to be able to respond with a move on time.
 - MAP_WIDTH and/or MAP_HEIGTH: changing this values may break the frontend or cause the fog not to close completely. Changing one or more of those values to a value smaller than 2 will crash the backend. Creating a map extremely narrow and long may also crash the game.
 - VM_INITIAL_GAS: setting this to a value too high or low may allow players to DOS the backend or be unable to run minions.
 - Any entity life/mana: it may break the frontend.

## Changing the default player bot

You can change the default player bot by editing the files in `docker_game_manager/docker_images/base_player_image/`.
Make sure to rebuild all the registries and run `push_basic_bots.py` again to store your changes.
Note that a basic bot is always needed or the manager may crash.
Make sure to copy the updated files in the `../dist/base_bot` directory, distributed to your players.