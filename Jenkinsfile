node {
  stage("Clone project") {
    git branch: 'develop', url: 'https://github.com/new052022/market-data-service.git'
  }

  stage("Build project with execution") {
    sh "./gradlew build"
  }
}