pipeline {
environment {
registry = "monacobot/market-data-service"
registryCredential = 'dockerhub-id'
dockerImage = ''
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
stage('Building our image') {
steps{
script {
    def dockerHome = tool 'docker'
        env.PATH = "${dockerHome}/bin:${env.PATH}"
dockerImage = docker.build registry + ":$BUILD_NUMBER"
}
}
}
stage('Pushing the image to Docker Hub') {
            steps {
                script {
                    // Use docker.withRegistry with your registry credential ID
                    docker.withRegistry('https://hub.docker.com/repository/docker/monacobot/market-data-service', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
}
stage('Deploy our image') {
steps{
script {
docker.withRegistry( '', registryCredential ) {
dockerImage.push()
}
}
}
}
stage('Cleaning up') {
steps{
sh "docker rmi $registry:$BUILD_NUMBER"
}
}
}
} 
