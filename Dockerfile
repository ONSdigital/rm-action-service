FROM openjdk:11-jre-slim

RUN apt-get update
COPY target/actionsvc.jar /opt/actionsvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/actionsvc.jar" ]


