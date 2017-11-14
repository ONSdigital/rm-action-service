FROM openjdk:8-jre

VOLUME /tmp
COPY target/actionsvc*.jar /opt/actionsvc.jar

ENTRYPOINT [ "java", "-jar", "/opt/actionsvc.jar" ]
