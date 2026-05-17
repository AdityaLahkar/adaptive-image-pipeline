pipeline {

    agent any

    environment {

        DOCKER_HUB_USERNAME = 'YOUR_DOCKER_USERNAME'
    }

    stages {

        stage('Clone Repository') {

            steps {

                checkout scm
            }
        }

        stage('Build API Image') {

            steps {

                sh 'docker build -t $DOCKER_HUB_USERNAME/api-service ./api-service'
            }
        }

        stage('Build Worker Image') {

            steps {

                sh 'docker build -t $DOCKER_HUB_USERNAME/worker-service ./worker-service'
            }
        }

        stage('Docker Login') {

            steps {

                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {

                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                }
            }
        }

        stage('Push API Image') {

            steps {

                sh 'docker push $DOCKER_HUB_USERNAME/api-service'
            }
        }

        stage('Push Worker Image') {

            steps {

                sh 'docker push $DOCKER_HUB_USERNAME/worker-service'
            }
        }
    }
}
