FROM openjdk:8-jre-slim

RUN useradd docker_user && \
    usermod -aG docker docker_user

USER docker_user

COPY actionsvc.jar /opt/actionsvc.jar. --chown=docker_user

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/actionsvc.jar" ]