FROM openjdk:8-jdk-alpine
EXPOSE 8080
COPY module-service-0.0.1-SNAPSHOT.jar app-module.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app-module.jar"]
