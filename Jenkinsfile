pipeline {
    environment {
        registry = "https://dailywish.pro/nexus_here/repository/market-data/"
        registryCredential = '471126e4-7c00-4009-8f6c-851b452ca24d'
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
                git branch: 'develop', // Specify develop branch
                   url: 'https://github.com/new052022/market-data-service.git'
            }
        }
        stage('Building the application') {
            steps {
                script {
                    // Execute Gradle command within the workspace
                    sh './gradlew bootBuildImage'
                }
            }
        }
        stage('Building our image') {
                  steps {
                      script {
                      dockerImage = docker.build("${registry}market-data-service:${BUILD_NUMBER}")
                      }
                  }
        }
        stage('Pushing the image to Nexus') {
            steps {
                script {
                    // Use docker.withRegistry with your Nexus registry credentials
                    docker.withRegistry('https://dailywish.pro/nexus_here/', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
        }
        stage('Deploy our image') {
            steps {
                script {
                    def imageName = registry + ":$BUILD_NUMBER"
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