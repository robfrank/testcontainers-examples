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

    stage('build support images') {
        ansiColor('xterm') {
            sh "cd ./src/main/docker/orientdb && ./build.sh && cd -"
            sh "cd ./src/main/docker/postgresql-dvdrental && ./build.sh && cd -"
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