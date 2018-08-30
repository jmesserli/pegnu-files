def branchSuffix() {
    return env.BRANCH_NAME == 'master' ? '' : '-' + env.BRANCH_NAME.replaceAll(/[^0-9A-Za-z-]+/, '-')
}

def fullVersion() {
    return env.VERSION + '.' + env.BUILD_NUMBER + branchSuffix()
}

pipeline {
    agent {
        docker {
            image 'jmesserli/oc-docker-build-container'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }

    environment {
        VERSION = readFile('VERSION')
    }

    stages {
        stage('Prepare') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean assemble configurationZip --stacktrace --info'
            }

            post {
                success {
                    archiveArtifacts './build/libs/pegnu-files-*.jar'
                    archiveArtifacts './build/distributions/pegnu-files-configuration*.zip'
                }
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test --stacktrace --info'
            }

            post {
                always {
                    junit './build/test-results/test/*.xml'
                }
            }
        }

        stage('Docker & Deploy') {
            when { branch 'master' }
            environment {
                DOCKER = credentials('docker-deploy')
                OCTOPUS_API_KEY = credentials('octopus-deploy')
                FULL_VERSION = fullVersion()
            }

            steps {
                sh 'docker login -u "$DOCKER_USR" -p "$DOCKER_PSW" docker.pegnu.cloud:443'
                sh 'docker build -t docker.pegnu.cloud:443/pegnu-files:latest -t docker.pegnu.cloud:443/pegnu-files:$FULL_VERSION .'
                sh 'docker push docker.pegnu.cloud:443/pegnu-files:latest && docker push docker.pegnu.cloud:443/pegnu-files:$FULL_VERSION'

                sh '/opt/octo/Octo push --package build/distributions/pegnu-files-configuration.$FULL_VERSION.zip --replace-existing --server https://deploy.pegnu.cloud --apiKey $OCTOPUS_API_KEY'
                sh '/opt/octo/Octo create-release --project "PegNu Files" --version $FULL_VERSION --package pegnu-files:$FULL_VERSION --package pegnu-files-configuration:$FULL_VERSION --server https://deploy.pegnu.cloud --apiKey $OCTOPUS_API_KEY'
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}