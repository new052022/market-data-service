Groovy
pipeline {
    environment {
        registry = "monacobot/market-data-service"
        registryCredential = 'monacobot'
        dockerImage = ''
    }
    agent any

    stages {
        stage('Cloning from develop branch') {
            steps {
                git branch: 'develop', // Explicitly specify develop branch
                   url: 'https://github.com/new052022/market-data-service.git'
            }
        }
        stage('Building our image') {
            steps {
                script {
                    // Construct image name dynamically
                    dockerImage = "${registry}:${BUILD_NUMBER}"
                    // Build the image using the dynamic name
                    docker.build(dockerImage)
                }
            }
        }
        stage('Tagging the image') {
            steps {
                script {
                    // Improved tagging format with optional registry URL
                    def imageName = "http://85.190.243.240:8083/repository/monaco_data_service:${BUILD_NUMBER}"
                    // Allow customization of registry URL (optional)
                    if (env. registryUrl ) {
                        imageName = "${env.registryUrl}/repository/monaco_data_service:${BUILD_NUMBER}"
                    }
                    docker.withRegistry('', registryCredential) {
                        dockerImage.tag(imageName)
                    }
                }
            }
        }
        stage('Pushing the tagged image') {
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push(imageName)
                    }
                }
            }
        }
        stage('Cleaning up') {
            steps {
                script {
                    // Remove both untagged and tagged images for cleaner execution
                    sh "docker rmi ${registry}:${BUILD_NUMBER} ${imageName}"
                }
            }
        }
    }
}
