FROM maven:3.9.1-amazoncorretto-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:17-jdk
VOLUME /tmp
COPY --from=build /home/app/target/*.jar cortex-bot.jar
ENTRYPOINT ["java","-jar","/cortex-bot.jar"]