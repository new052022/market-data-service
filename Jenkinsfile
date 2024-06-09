pipeline {
environment {
registry = "monacobot/market-data-service"
registryCredential = 'docker-id'
dockerImage = ''
}
agent { docker { image 'openjdk:21-jdk'} }

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
dockerImage = docker.build registry + ":$BUILD_NUMBER"
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
