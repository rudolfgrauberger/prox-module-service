pipeline {
    agent {
        docker {
            image 'maven:3.6.1-jdk-8-alpine'
        }
    }
    environment {
        REPOSITORY = "ptb-gp-ss2019.archi-lab.io"
        IMAGE = "module-service"
    }
    stages {
        stage("Build") {
            steps {
                sh "mvn clean install -X" // Führt den Maven build aus
            }
        }
        stage('SonarQube Analysis') {
            steps {
                sh "ifconfig"
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
            steps {
                sh "cat /etc/hosts"
            }
        }
    }
}
