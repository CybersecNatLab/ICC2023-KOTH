#!/bin/sh

# TODO: Replace the following two variables with your team and password before executing

# One of africa asia canada europe latina oceania usa
team=substitute with your team shortname

# Provided by the competition orgs before the competition start
password=substitute with your team password

docker login https://$team.koth.icc.cybersecnatlab.it -u $team -p $password

docker build -t bot:latest -f Dockerfile .
docker tag bot:latest $team.koth.icc.cybersecnatlab.it/bot/bot:latest
docker push $team.koth.icc.cybersecnatlab.it/bot/bot:latest