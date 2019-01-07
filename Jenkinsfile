#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven ''
        jdk ''
    }
    environment {

    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out..'
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
