pipeline {
    agent {
        docker {
            image '3.6.1-jdk-8-alpine'
        }
    }
    environment {
        REPOSITORY = "ptb-gp-ss2019.archi-lab.io"
        IMAGE = "module-service"
    }
    stages {
        stage("Build") {
            steps {
                sh "mvn clean install" // Führt den Maven build aus
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
            steps {
                sh "cat /etc/hosts"
            }
        }
    }
}
