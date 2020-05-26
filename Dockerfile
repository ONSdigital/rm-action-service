FROM openjdk:8-jre-slim

RUN apt-get update
COPY target/actionsvc-UNVERSIONED.jar /opt/actionsvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/actionsvc.jar" ]


