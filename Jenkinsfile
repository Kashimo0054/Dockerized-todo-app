pipeline {
    agent { label 'docker1' }  // Host VM agent label

    environment {
        // Use Temurin 21 explicitly
        JAVA_HOME = '/usr/lib/jvm/temurin-21-jdk'
        PATH = "/opt/maven/bin:${JAVA_HOME}/bin:${env.PATH}"

        // Docker image info
        IMAGE_NAME = 'todo-springboot-app'
        IMAGE_TAG = "v${BUILD_NUMBER}"

        // Artifactory config
        ARTIFACTORY_SERVER_ID = 'bitwranglers'
        ARTIFACTORY_URL = 'https://bitwranglers.jfrog.io/artifactory'
        ARTIFACTORY_REPO = 'docker-repo'
    }

    stages {

        stage('Verify Java & Maven') {
            steps {
                sh 'echo "JAVA_HOME=$JAVA_HOME"
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Kashimo0054/Dockerized-todo-app.git'
            }
        }

        stage('Build Maven Project') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    echo "Building Docker image ${IMAGE_NAME}:${IMAGE_TAG}"
                    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                """
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
                sh """
                    echo "Stopping existing containers if any..."
                    docker compose down || true
                    echo "Starting containers..."
                    docker compose up -d --build
                """
            }
        }
    }

    post {
        success {
            echo "🎉 Deployment Successful!"
        }
        failure {
            echo "❌ Build Failed — check the logs."
        }
    }
}
