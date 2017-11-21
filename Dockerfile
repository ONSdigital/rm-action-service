FROM openjdk:8-jre

VOLUME /tmp
COPY target/actionsvc*.jar /opt/actionsvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/actionsvc.jar" ]


