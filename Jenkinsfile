#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven "apache-maven-3.6.0"
    }
    stages {
        stage("Checkout") {
            steps {
                echo "Checking out...."
            }
        }
        stage("Build") {
            steps {
                sh "mvn clean package" // Führt den Maven build aus
            }
        }
        stage("Test") {
            steps {
                echo "Testing..."
            }
        }
        stage("Deploy") {
            steps {
                echo "Deploying..."
            }
        }
    }
}
