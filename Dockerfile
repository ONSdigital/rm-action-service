FROM openjdk:8-jre-slim

WORKDIR /opt

COPY target/actionsvc*SNAPSHOT.jar /opt/actionsvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/actionsvc.jar" ]


