FROM openjdk:8-jdk-alpine
EXPOSE 9001
COPY module-service-0.0.1-SNAPSHOT.jar app-feedback.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app-feedback.jar"]
