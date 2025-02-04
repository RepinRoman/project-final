#
# Build stage
#
FROM maven:3.8.5-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install -P prod

#
# Package stage
#
FROM openjdk:17.0.2-jdk-slim-buster
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
COPY resources ./resources
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]