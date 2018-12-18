FROM openjdk:8-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/module-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app-module.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app-module.jar"]
