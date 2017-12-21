ARG JAR_FILE=actionsvc*.jar
FROM openjdk:8-jre

VOLUME /tmp
ARG JAR_FILE
COPY target/$JAR_FILE /opt/actionsvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/actionsvc.jar" ]


