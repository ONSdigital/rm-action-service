FROM maven:3.6.3-jdk-11-slim

RUN apt-get update
RUN apt-get install docker-compose


