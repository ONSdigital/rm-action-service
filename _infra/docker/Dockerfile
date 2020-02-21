FROM openjdk:8-jre-slim

COPY actionsvc.jar /opt/actionsvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/actionsvc.jar" ]