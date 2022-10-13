pipeline {

    agent any
    stages {
        stage('Build') {
            steps {
                sh './gradle build'
            }
        }


        stage('Docker') {
            steps {
                sh './gradlew jib'
            }
        }
        stage('DeployLocal') {
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