pipeline {
    agent {
        docker {
            image 'maven:3.6.1-jdk-8-alpine'
            args '-v maven-data:/root/.m2'
        }
    }
    stages {
        stage("Build") {
            steps {
                sh "mvn clean compile"
            }
        }
        stage('SonarQube Analysis') {
            steps {
                echo "SonarQube..."
            }
        }
        stage("Test") {
            steps {
                sh "mvn test"
            }
        }
        stage("Code Quality Check") {
            steps {
                echo "Code Quality Check..."
            }
        }
        stage("Deploy") {
            steps {
                sh "mvn -Dmaven.test.skip=true -Dmaven.install.skip=true -Dmaven.deploy.skip=true deploy"
            }
        }
    }
}
