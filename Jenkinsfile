pipeline {
    agent {
        docker {
            image 'maven:3.6.1-jdk-8-alpine'
            args '-v maven-data:/root/.m2'
        }
    }
    environment {
        REPOSITORY = "ptb-gp-ss2019.archi-lab.io"
        IMAGE = "module-service"
    }
    stages {
        stage("Build") {
            steps {
                sh "mvn -Dmaven.install.skip=true clean install" // Führt den Maven build aus
            }
        }
        stage('SonarQube Analysis') {
            steps {
                sh "ifconfig"
                sh "cd ./target && ls -ls"
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
