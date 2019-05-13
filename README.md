# PTB Module Service
The purpose of this service is to provide a backend for study courses and modules, relevant for projects and topics inside the "Projekt- und Themenbörse (PTB)" web application of TH Köln. 

## Installation
```
mvn clean package
```
Executes the [Maven default lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html) up to the package phase. During `package` phase a runnable JAR is created and a docker image is build.

### Deploy (from your local system)
To push the image to our own docker registry, you have to define a server with the id `docker.nexus.archi-lab.io` with your username and password for authentication in the [maven settings.xml](https://maven.apache.org/guides/mini/guide-configuring-maven.html#Security_and_Deployment_Settings).

``` xml
<settings>
  <servers>
    <server>
      <id>docker.nexus.archi-lab.io</id>
      <username>your-username</username>
      <password>your-password</password>
    </server>
  </servers>
</settings>
```

Then you can push the image by running the `deploy` phase.

``` bash
mvn -Dmaven.deploy.skip=true deploy
```

`-Dmaven.deploy.skip=true` ensures that the default behavior of the maven deploy phase is not executed. However, the attached maven plugin to this phase still execute and pushes the image to the private docker repository.

## Usage
```
docker-compose -f docker-compose-module-service.yml up
```
Starts a Docker container based on the compose file and the image. A Docker network named `ptb` is required for the communication between services:
```
docker network create ptb
```
It is also possible to run the JAR directly with specific Spring profiles:
```
java -jar -Dspring.profiles.active=local target/module-service-0.0.1-SNAPSHOT.jar
```

## Documentation
You can view the full documentation under the following [GitHub Wiki](https://github.com/Archi-Lab/ptb-documentation/wiki).

## Contributing

## About the Team
This service is currently developed by the PTB Team composed of students from [TH Köln](https://www.th-koeln.de/):

- Julian Lengelsen ([@jlengelsen](https://github.com/jlengelsen))
- Mansoor Rahmati ([@ManSoorSour](https://github.com/ManSoorSour))
- Jan Seidler ([@janseidler](https://github.com/janseidler))
