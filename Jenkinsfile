pipeline {
environment {
registry = "monacobot/market-data-service"
registryCredential = 'docker-id'
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
stage('Building the application') {
      steps {
        script {
          // Execute Gradle command within the workspace
           sh './gradlew bootBuildImage'
        }
      }
}
stage('Building our image') {
steps{
script {
    def dockerHome = tool 'docker'
        env.PATH = "${dockerHome}/bin:${env.PATH}"
dockerImage = docker.build registry + ":$BUILD_NUMBER"
   def postgresUser = env.POSTGRES_USER
            def postgresPass = env.POSTGRES_PASS
            def dbHost = env.DB_HOST
            def secretNumber = env.SECRET_NUMBER
            def algorithm = env.ALGORITHM
  sh """
            docker build -t ${registry}:$BUILD_NUMBER \
                --build-arg POSTGRES_USER=${postgresUser} \
                --build-arg POSTGRES_PASS=${postgresPass} \
                --build-arg DB_HOST=${dbHost} \
                --build-arg SECRET_NUMBER=${secretNumber} \
                --build-arg ALGORITHM=${algorithm} .
            """
}
}
}
stage('Pushing the image to Docker Hub') {
            steps {
                script {
                    // Use docker.withRegistry with your registry credential ID
                    docker.withRegistry('https://registry.hub.docker.com', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
}
stage('Deploy our image') {
   steps {
        script {
           
            def imageName = registry + ":$BUILD_NUMBER"
            sh """
                docker run -d -p 9001:9001 ${imageName}
            """
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
