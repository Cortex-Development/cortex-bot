FROM openjdk:15.0.1-jdk-buster
VOLUME /tmp
COPY target/*.jar cortex-bot-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/cortex-bot-0.0.1-SNAPSHOT.jar"]