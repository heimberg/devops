pipeline {

    agent any

    environment {
    CLOUDSDK_CORE_PROJECT='cellular-syntax-231507'
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
                withSonarQubeEnv('SonarQube') {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew sonarqube -D"sonar.projectKey=DevOps"'
                }
            }
        }
        stage('quality gate') {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('create docker image and push to registry') {
            steps {
                withCredentials([file(credentialsId: 'gcloud', variable: 'GCLOUD')]) {
                    sh '''
                        gcloud auth activate-service-account --key-file="$GCLOUD"
                        ./gradlew jib
                    '''
                }
            }
        }
        // deploy to google cloud run on port 7000
        stage('Deploy') {
            steps {
                withCredentials([file(credentialsId: 'gcloudcompute', variable: 'GCLOUDCOMPUTE')]) {
                    sh '''
                        gcloud auth activate-service-account --key-file="$GCLOUDCOMPUTE"
                        gcloud run deploy devops --image gcr.io/cellular-syntax-231507/devops --platform managed --region europe-west4 --allow-unauthenticated --port 7000 --service-account 382263290396-compute@developer.gserviceaccount.com 	

                    '''
                }
            }
        }
    }

    post {
        always {
            emailext (
                subject: 'Jenkins build: $BUILD_STATUS',
                body: '$BUILD_URL',
                to: 'matthias.heimberg@students.ffhs.ch'
            )
            // archive the artifacts
            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
        }
    }
}