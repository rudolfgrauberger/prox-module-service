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
                echo "Testing....."
            }
        }
        stage("Code Quality Check") {
            steps {
                echo "Code Quality Check...."
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
                sh "scp -P ${SERVERPORT} -v ${PROJECTNAME}.tar ${SSHUSER}@${SERVERNAME}:~/"     // Kopiert per ssh die tar Datei auf dem Produktionsserver
                sh "scp -P ${SERVERPORT} -v ${YMLFILENAME} ${SSHUSER}@${SERVERNAME}:/srv/projektboerse/"
                sh "ssh -p ${SERVERPORT} ${SSHUSER}@${SERVERNAME} " +
                        "'docker image load -i ${PROJECTNAME}.tar; " +
                        /*"docker network inspect ptb_infra-net &> /dev/null || docker network create ptb_infra-net; " + */ // when connecting to other services, enable this
                        "docker-compose -p ptb -f /srv/projektboerse/${YMLFILENAME} up -d'"
            }
        }
    }
}
