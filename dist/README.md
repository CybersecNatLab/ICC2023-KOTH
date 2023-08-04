# KOTH - Dist

This folder contains the basic files distributed to the participants of the ICC 2023 KOTH.
Here you can find:

- `ICC2023-KOTH.jar`, the game engine which runs remotely and that manages all the game logic and communication between teams bot. This is the file that you need to analyze in order to find the vulnerabilities.
- `config.json`: a set of config overrides that will be loaded by the game engine, to learn and understand all the possible configurations please have a look in the jar file.
- `Dockerfile` and `docker-compose.yml`: a basic docker container to test your bot locally as if you were in the remote environment. This container will launch 2 istances of your bot and the game engine makes them compete against each other. Please note that this is not the real remote infrastructure but only a test bench.

## VMCodeCompiler

To help you generate the minions shellcode we provide you with a simple vm code compiler. Inside the VMCodeCompiler folder you can find the following files:

- `compiler.py`: A simple compiler to help you generate the minions shellcode.
- `config.py`: A configuration file for the compiler containing all the instructions and parameters.
- `requirements.txt`: python packages needed to run `compiler.py`.
- `examples/`: A folder containing two simple input and output examples of minion shellcode.

You can use the code compiler by installing the dependecies with `pip3 install -r requirements.txt` and by running `python3 compiler.py source.txt output.json` with your custom `source.txt` file.

## Base bot

To help you get started we provide you a basic bot already pushed to your registry that will be running from the beginning until your first submission. Inside the `base_bot` folder you can find the following files:

- `Dockerfile`: the Dockerfile of the container needed to be uploaded to your registry.
- `push.sh`: the script to upload your bot container to your registry.
- `RPC_adapter.py`: the base python script that manage the websocket connection with the game engine and execute the methods of your bot.
- `bot.py`: the actual bot logic you need to implement. It implements a `move` method receiving the `map` matrix and return a json with the action.
- `constants.py`: a config files containing all the needed game constants.

## How to upload your bot

You can upload your bot by simply running the `push.sh` script after replacing your team shortname and password needed for the registry autentication.

Note: your bot will be runned starting from the new round.
