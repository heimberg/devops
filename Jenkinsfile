pipeline {

    agent none

    environment {
    CLOUDSDK_CORE_PROJECT='cellular-syntax-231507'
    } 

    options {
        gitLabConnection('GitLab DevOps')
        gitlabCommitStatus(name: 'Jenkins')
        gitlabBuilds(builds: ['checkout from GitLab', 'check code quality', 'quality gate', 'build', 'create docker image and push to registry', 'deploy to cloud run'])
    }

    stages {
        stage('checkout from GitLab') {
            agent any
            steps {
                gitlabCommitStatus(name: 'checkout from GitLab') {
                    withCredentials([usernamePassword(credentialsId: 'gitlab-access', usernameVariable: 'GITLAB_USER', passwordVariable: 'GITLAB_PASSWORD')]) {
                        git url: 'https://git.ffhs.ch/matthias.heimberg/devops.git', branch: 'develop', credentialsId: 'gitlab-access'
                    }
                }
            }
        }
        stage('check code quality') {
            agent any
            steps {
                gitlabCommitStatus(name: 'check code quality') {
                    withSonarQubeEnv('SonarQube') {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew sonarqube -D"sonar.projectKey=DevOps"'
                    }
                }
            }
        }
        stage('quality gate') {
            agent any
            steps {
                gitlabCommitStatus(name: 'quality gate') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('build') {
            agent any
            steps {
                gitlabCommitStatus(name: 'build') {
                    sh './gradlew build'
                }
            }
        }
        stage('create docker image and push to registry') {
            agent any
            steps {
                gitlabCommitStatus(name: 'create docker image and push to registry') {
                    withCredentials([file(credentialsId: 'gcloud', variable: 'GCLOUD')]) {
                        sh '''
                            gcloud auth activate-service-account --key-file="$GCLOUD"
                            ./gradlew jib
                        '''
                    }
                }
            }
        }
        // deploy to google cloud run on port 7000
        stage('deploy to cloud run') {
            agent any
            steps {
                gitlabCommitStatus(name: 'deploy to cloud run') {
                    withCredentials([file(credentialsId: 'gcloudcompute', variable: 'GCLOUDCOMPUTE')]) {
                        sh '''
                            gcloud auth activate-service-account --key-file="$GCLOUDCOMPUTE"
                            gcloud run deploy devops --image gcr.io/cellular-syntax-231507/devops --platform managed --region europe-west4 --allow-unauthenticated --port 7000 --service-account 382263290396-compute@developer.gserviceaccount.com 	

                        '''
                    }
                }
            }
        }

        // test with jmeter inside docker container (jenkins container binds to docker socket on host)
        stage('test with jmeter') {
            agent { 
                docker {
                    image 'justb4/jmeter:5.1.1'
                    args '-v /var/jenkins_home/jmeter-data:/home/user/jmeter'
                }
            }
            steps {
                gitlabCommitStatus(name: 'test with jmeter') {
                    sh '''
                        export TIMESTAMP=$(date +%Y%m%d_%H%M%S)
                        jmeter -n -t check_api.jmx -l result_${TIMESTAMP}.jtl -j jmeter_${TIMESTAMP}.log
                    '''
                }
            }
        }
    }

    post {
        success {
            // archive the artifacts
            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
        }
        always {
            emailext (
                subject: 'Jenkins build: $BUILD_STATUS',
                body: '$BUILD_URL',
                from: 'jenkins',
                to: 'matthias.heimberg@students.ffhs.ch'
            )
            
        }
    }
}