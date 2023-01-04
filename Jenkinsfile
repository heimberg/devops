pipeline {

    agent any

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
            steps {
                gitlabCommitStatus(name: 'checkout from GitLab') {
                    withCredentials([usernamePassword(credentialsId: 'gitlab-access', usernameVariable: 'GITLAB_USER', passwordVariable: 'GITLAB_PASSWORD')]) {
                        git url: 'https://git.ffhs.ch/matthias.heimberg/devops.git', branch: 'develop', credentialsId: 'gitlab-access'
                    }
                }
            }
        }
        stage('check code quality') {
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
            steps {
                gitlabCommitStatus(name: 'quality gate') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('build') {
            steps {
                gitlabCommitStatus(name: 'build') {
                    sh './gradlew build'
                }
            }
        }
        stage('create docker image and push to registry') {
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
            steps {
                gitlabCommitStatus(name: 'test with jmeter') {
                    sh '''
                        export TIMESTAMP=$(date +%Y%m%d_%H%M%S)
                        export JMETER_PATH=/mnt/jmeter
                        export JMETER_FILE=check_api.jmx
                        docker run --rm -v "${PWD}"/jmeter-data:"${JMETER_PATH}" justb4/jmeter -n -t /mnt/jmeter/scripts/"${JMETER_FILE}" -l "${JMETER_PATH}"/tmp/result_"${TIMESTAMP}".jtl -j "${JMETER_PATH}/tmp/jmeter_${TIMESTAMP}".log 
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