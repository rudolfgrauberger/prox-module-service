#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven "apache-maven-3.6.0"
        jdk "JDK_8u191"
    }
    environment {
        PROJECTNAME = "module-service"
    }
    stages {
        stage("Build") {
            steps {
                sh "mvn clean package" // Führt den Maven build aus
                sh "docker build -t ${PROJECTNAME} ." // baut die Java App auf dem Container
                sh "docker image save -o ${PROJECTNAME}.tar ${PROJECTNAME}" // Docker image als tar Datei speichern
            }
        }
        stage('SonarQube Analysis') {
            steps {
                script {
                    // requires SonarQube Scanner 3.2+
                    scannerHome = tool 'TH Koeln GM SonarQube Scanner'
                }
                withSonarQubeEnv('TH Koeln GM SonarQube') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }
            }
        }
        stage("Test") {
            steps {
                echo "Testing..."
            }
        }
        stage("Code Quality Check") {
            steps {
                echo "Code Quality Check..."
            }
        }
        stage("Deploy") {
            environment {
                SERVERPORT = "22412"
                YMLFILENAME = "docker-compose.yml"
                SSHUSER = "jenkins"
                SERVERNAME = "fsygs15.inf.fh-koeln.de"
            }
            steps {
                sh "scp -P ${SERVERPORT} -v ${PROJECTNAME}.tar ${SSHUSER}@${SERVERNAME}:~/"     // Kopiert per ssh die tar Datei auf dem Prod Server
                sh "scp -P ${SERVERPORT} -v ${YMLFILENAME} ${SSHUSER}@${SERVERNAME}:/srv/projektboerse/"
                sh "ssh -p ${SERVERPORT} ${SSHUSER}@${SERVERNAME} " +
                        "'docker image load -i ${PROJECTNAME}.tar; " +
                        "docker network inspect backend-network &> /dev/null || docker network create backend-network; " +
                        "docker network inspect module-service_db &> /dev/null || docker network create module-service_db; " +
                        "docker-compose -p ptb -f /srv/projektboerse/${YMLFILENAME} up -d'"
            }
        }
    }
}
