FROM maven:3.6.3-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:17-jdk
VOLUME /tmp
COPY --from=build /home/app/target/*.jar cortex-bot.jar
ENTRYPOINT ["java","-jar","/cortex-bot.jar"]