#!/usr/bin/env groovy

node {

    cleanWs()

    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        ansiColor('xterm') {
            sh "java -version"
        }
    }

    stage('build') {
        try {
            ansiColor('xterm') {
                sh "./mvnw clean install -fae"
            }
        } catch (err) {
            throw err
        } finally {
            junit '**/target/surefire-reports/**/*.xml'

        }
    }
}