pipeline {
    agent { label 'docker1' }

    tools {
        jdk 'temurin-21'   // Use the actual JDK name in Jenkins
        // Maven is optional if you use system Maven, otherwise configure Maven in Jenkins tools
        // maven 'MAVEN_HOME'  // Uncomment if you added Maven tool in Jenkins
    }

    environment {
        IMAGE_NAME = 'todo-springboot-app'
        IMAGE_TAG = "v${BUILD_NUMBER}"
        ARTIFACTORY_SERVER_ID = 'bitwranglers'
        ARTIFACTORY_REPO = 'docker-repo'
        ARTIFACTORY_CREDENTIALS = 'jfrog-creds'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/Kashimo0054/Dockerized-todo-app.git'
            }
        }

        stage('Build Maven Project') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push Docker Image to Artifactory') {
            steps {
                script {
                    def server = Artifactory.server(ARTIFACTORY_SERVER_ID)
                    def rtDocker = Artifactory.docker(server: server)
                    rtDocker.push("${IMAGE_NAME}:${IMAGE_TAG}", ARTIFACTORY_REPO)
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker compose down || true'
                sh 'docker compose up -d --build'
            }
        }
    }

    post {
        success { echo "🎉 Deployment Successful!" }
        failure { echo "❌ Build Failed — check the logs." }
    }
}
