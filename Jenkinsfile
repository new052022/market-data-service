pipeline {
    environment {
        registry = "moritz007/market-data"
        registryCredential = 'docker-hub-credentials'
    }
    tools {
        jdk 'JDK 21'
    }
    agent {
        label 'built-in'
    }
    stages {
        stage('Clean old containers') {
            steps {
                script {
                    sh '''
                    docker ps -a -q --filter "ancestor=moritz007/market-data" | xargs -r docker stop
                    docker ps -a -q --filter "ancestor=moritz007/market-data" | xargs -r docker rm
                    docker images -q "moritz007/market-data" | xargs -r docker rmi -f
                    '''
                }
            }
        }

        stage('Cloning our Git') {
            steps {
                git branch: 'develop', url: 'https://github.com/new052022/market-data-service.git'
            }
        }

        stage('Building the application') {
            steps {
                script {
                    sh './gradlew bootBuildImage'
                }
            }
        }

        stage('Building our image') {
            steps {
                script {
                    dockerImage = docker.build("${registry}:${BUILD_NUMBER}")
                }
            }
        }

        stage('Pushing the image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
        }

        stage('Deploy our image') {
            steps {
                script {
                    withEnv([
                        "POSTGRES_USER=${env.POSTGRES_USER}",
                        "POSTGRES_PASS=${env.POSTGRES_PASS}",
                        "DB_HOST=${env.DB_HOST}",
                        "SECRET_NUMBER=${env.SECRET_NUMBER}",
                        "ALGORITHM=${env.ALGORITHM}"
                    ]) {
                        sh '''
                        docker ps -f name=market-data-service -q | xargs --no-run-if-empty docker stop
                        docker ps -a -f name=market-data-service -q | xargs --no-run-if-empty docker rm

                        docker run -d --name market-data-service -p 9001:9001 \
                            -e POSTGRES_USER=$POSTGRES_USER \
                            -e POSTGRES_PASS=$POSTGRES_PASS \
                            -e DB_HOST=$DB_HOST \
                            -e SECRET_NUMBER=$SECRET_NUMBER \
                            -e ALGORITHM=$ALGORITHM \
                            moritz007/market-data:${BUILD_NUMBER}
                        '''
                    }
                }
            }
        }

        stage('Cleaning up') {
            steps {
                script {
                    sh "docker rmi -f ${registry}:${BUILD_NUMBER}"
                }
            }
        }
    }
}