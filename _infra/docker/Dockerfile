FROM openjdk:8-jre-slim

RUN groupadd docker && \
    useradd docker_user && \
    usermod -aG docker docker_user

USER docker_user

COPY actionsvc.jar /opt/actionsvc.jar.

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/actionsvc.jar" ]