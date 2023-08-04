FROM ubuntu:22.04

RUN apt update && apt upgrade -y

RUN apt-get install -y socat
RUN apt-get install -y libssl-dev
RUN apt install openjdk-19-jdk -y

COPY ICC2023-KOTH.jar /KOTH.jar

ENTRYPOINT [ "java", "-jar", "/KOTH.jar", "/config.json" ]