# KOTH insecure registries deploy

The following instructions are to be followed only to deploy the KOTH game without HTTPS.
Beware that this is an <strong>insecure</strong> configuration and should only be used in safe testing environments.

You should update the `docker-compose.yml` file in this directory to directly expose the ports of all services and registries, ensuring no overlap, removing all traefik related labels.

Each player should set up their docker configuration to include the addresses of all registries into the insecure registries list.
You can find instructions on how to do so [here](https://docs.docker.com/registry/insecure/). 
Moreover, also the server where the infrastructure is running should have the configuration set up the same way.

Then, script `push_basic_bots.py` should be updated to correctly connect to the registries by using a different port for each team instead of a different domain name. Specifically lines 18 to 24 should be updated with the correct ip+port variables.

Finally, script `docker_game_manager/manager.py` should be updated in the same way. Specifically function `update_images` defined at line 93.

You can now return following the original README.md, ignoring all HTTPS and traefik requirements.