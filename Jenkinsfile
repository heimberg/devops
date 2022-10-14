pipeline {

    agent any
    // check repo for changes
    triggers {
        pollSCM('H/5 * * * *')
    }
    stages {
        stage('checkout from GitLab') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'gitlab-access', usernameVariable: 'GITLAB_USER', passwordVariable: 'GITLAB_PASSWORD')]) {
                    git url: 'https://git.ffhs.ch/matthias.heimberg/devops.git', branch: 'develop', credentialsId: 'gitlab-access'
                }
            }
        }
        stage('check code quality') {
            steps {
                script {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew sonarqube -D"sonar.projectKey=DevOps" -D"sonar.host.url=http://localhost:9000" -D"sonar.login=sqp_dcb4582512ce4565cbc572871de81ecaa8f9e5fd"'
                }
            }
        }
        stage('Build') {
            steps {
                sh './gradle build'
            }
        }


        stage('create docker image and push to registry') {
            steps {
                sh './gradlew jib'
            }
        }
        stage('DeployLocal') {
            // only with flag -DdeployLocal=true
            when {
                expression { return params.deployLocal }
            }
            steps {
                sh 'docker run -p 7000:7000 heimberg/devops:latest'
            }
        }
        // deploy to google cloud run on port 7000
        stage('Deploy') {
            steps {
                sh 'gcloud run deploy devops --image gcr.io/cellular-syntax-231507/devops --platform managed --region europe-west4 --allow-unauthenticated --port 7000'
            }
        }
    }
}