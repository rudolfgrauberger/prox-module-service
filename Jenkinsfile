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
        stage("Checkout") {
            steps {
                checkout([
                        $class: "GitSCM",
                        branches: [[name: "*/master"]],
                        credentialsId: "",
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [],
                        submoduleCfg: [],
                        userRemoteConfigs: [[credentialsId: '632cd510-b0ca-4567-8f61-5e134a8cff98', url: 'https://fsygs15.gm.fh-koeln.de:8888/PTB/module-service.git']]
                ])
            }
        }
        stage("Build") {
            steps {
                sh "mvn clean package" // Führt den Maven build aus
            }
        }
        stage('SonarQube Analysis') {
            withSonarQubeEnv('TH Koeln GM SonarQube') {
                // requires SonarQube Scanner for Maven 3.2+
                sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'
            }
        }
        stage("Test") {
            steps {
                echo "Testing......."
            }
        }
        stage("Code Quality Check") {
            steps {
                sh "mvn checkstyle:checkstyle"
            }
            post {
                always {
                    step([$class: "hudson.plugins.checkstyle.CheckStylePublisher", pattern: "**/target/checkstyle-result.xml", unstableTotalAll: "100"])
                }
            }
        }
        stage("Deploy") {
            steps {
                echo "Deploying..."
            }
        }
    }
}
