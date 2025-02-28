pipeline {
    agent any
   
    environment{
        SCANNER_HOME= tool 'sonar-scanner'
    }

    stages {
        stage('git-checkout') {
            steps {
                git branch: 'main', changelog: false, poll: false, url: 'https://github.com/jaiswaladi246/Petclinic.git'
            }
        }

    stage('Sonar Analysis') {
            steps {
                     withSonarQubeEnv('sonar-server') {
                    sh ''' $SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName=to-do-app \
                    -Dsonar.java.binaries=. \
                    -Dsonar.projectKey=to-do-app '''
               }
            }
                
            }
     stage('Docker Build') {
            steps {
               script{
                   withDockerRegistry(credentialsId: 'docker-login') {
                    sh "docker stop to-do-app "
                    sh "docker rm to-do-app "
                    sh "docker build -t  todoapp:latest -f backend/Dockerfile . "
                    sh "docker tag todoapp:latest kiranpkdocker/todoapp:latest "
                    sh "docker push  kiranpkdocker/todoapp:latest "
                    sh "docker run -d --name to-do-app -p 3000:3000 kiranpkdocker/todoapp:latest "
                 }
               }
            }
        }            
}
}
