node {
  stage("Clone project") {
    git branch: 'develop', url: 'https://github.com/new052022/market-data-service.git'
  }

  stage("Build project with execution") {
    sh "./gradlew build"
  }

  stage('Build Docker Image (Optional)') {
    steps {
        script {
            sh 'docker build -t market-data-0.0.1-SNAPSHOT:latest .' 
        }
    }
}
}
