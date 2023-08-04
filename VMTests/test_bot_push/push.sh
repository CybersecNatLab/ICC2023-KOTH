#! /bin/sh

team="usa"

docker build -t bot:latest -f Dockerfile .
docker tag bot:latest $team.koth.icc.cybersecnatlab.it/bot/bot:latest
docker push $team.koth.icc.cybersecnatlab.it/bot/bot:latest