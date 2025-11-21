pipeline {
    agent { label 'docker1' }

    environment {
        ARTIFACTORY_URL  = "bitwranglers.jfrog.io"
        ARTIFACTORY_REPO = "docker-repo"
        IMAGE_NAME       = "todo-springboot-app"
        DEPLOY_PATH      = "/home/jenkins-agent/deploy/todoapp"   // where compose runs
    }

    stages {

        stage('Checkout Deployment Repo') {
            steps {
                sh "mkdir -p ${DEPLOY_PATH}"
                dir("${DEPLOY_PATH}") {
                    git branch: 'main',
                        url: 'https://github.com/Kashimo0054/Dockerized-todo-app.git'
                }
            }
        }

        stage('Pull Latest Image from Artifactory') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'artifactory-cred',
                    usernameVariable: 'ART_USER',
                    passwordVariable: 'ART_PASS'
                )]) {

                    sh """
                        echo "📥 Logging in to Artifactory..."
                        echo $ART_PASS | docker login ${ARTIFACTORY_URL} -u $ART_USER --password-stdin

                        echo "📥 Pulling latest image..."
                        docker pull ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${IMAGE_NAME}:latest

                        echo "🔐 Logging out..."
                        docker logout ${ARTIFACTORY_URL}
                    """
                }
            }
        }

        stage('Deploy New Version') {
            steps {
                dir("${DEPLOY_PATH}") {
                    sh """
                        echo "🛑 Stopping running containers..."
                        docker compose down || true

                        echo "🚀 Starting with latest image..."
                        docker compose up -d
                    """
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo "🔍 Checking if application is healthy..."

                    def tries = 0
                    def healthy = false

                    while (tries < 10) {
                        def status = sh(
                            script: "curl -s -o /dev/null -w '%{http_code}' http://springboot-app:8084/actuator/health",
                            returnStdout: true
                        ).trim()

                        if (status == "200") {
                            healthy = true
                            break
                        }

                        sleep 5
                        tries++
                    }

                    if (!healthy) {
                        error("❌ Deployment FAILED — Health check did not pass.")
                    } else {
                        echo "✅ Health check passed — Deployment successful!"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🎉 CD Deployment Successful!"
        }
        failure {
            echo "❌ Deployment FAILED — rollback recommended."
        }
    }
}
