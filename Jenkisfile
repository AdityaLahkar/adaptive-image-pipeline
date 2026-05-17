pipeline {

    agent any

    stages {

        stage('Clone Repository') {

            steps {

                checkout scm
            }
        }

        stage('Build API Image') {

            steps {

                sh 'docker build -t api-service ./api-service'
            }
        }

        stage('Build Worker Image') {

            steps {

                sh 'docker build -t worker-service ./worker-service'
            }
        }
    }
}
