pipeline {
    environment {
        registry = "moritz007/market-data"
        registryCredential = 'docker-hub-credentials'
        dockerImage = ''
    }
    tools {
        jdk 'JDK 21'
    }
    agent {
        label 'built-in'
    }
    stages {
        stage('Cloning our Git') {
            steps {
                git branch: 'develop', url: 'https://github.com/new052022/market-data-service.git' // Репозиторий
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
                    def imageName = "${registry}:${BUILD_NUMBER}"
                    def postgresUser = env.POSTGRES_USER
                    def postgresPass = env.POSTGRES_PASS
                    def dbHost = env.DB_HOST
                    def secretNumber = env.SECRET_NUMBER
                    def algorithm = env.ALGORITHM

                    sh 'docker ps -f name=market-data-service -q | xargs --no-run-if-empty docker container stop'
                    sh 'docker container ls -a -fname=market-data-service -q | xargs -r docker container rm'

                    sh """
                    docker run -d --name market-data-service -p 9001:9001 -e POSTGRES_USER=${postgresUser} -e POSTGRES_PASS=${postgresPass} -e DB_HOST=${dbHost} -e SECRET_NUMBER=${secretNumber} -e ALGORITHM=${algorithm} ${imageName}
                    """
                }
            }
        }
        stage('Cleaning up') {
            steps {
                sh "docker rmi $registry:$BUILD_NUMBER"
            }
        }
    }
}