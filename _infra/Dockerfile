FROM openjdk:8-jre-slim

RUN groupadd --gid 999 action && \
    useradd --create-home --system --uid 999 --gid action action
USER action

COPY actionsvc.jar /opt/actionsvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/actionsvc.jar" ]