FROM openjdk:8u191-jdk-alpine3.8
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-cp","app:app/lib/*","io.archilab.projektboerse.moduleservice.ModuleService"]
