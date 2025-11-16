pipeline {
    agent { label 'docker1' }  // your Jenkins agent label

    environment {
        // Java & Maven paths
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "/opt/maven/bin:${JAVA_HOME}/bin:${PATH}"

        // Docker image info
        IMAGE_NAME = 'todo-springboot-app'
        IMAGE_TAG = "v${BUILD_NUMBER}"

        // Artifactory configuration
        ARTIFACTORY_SERVER_ID = 'bitwranglers'
        ARTIFACTORY_URL = 'https://bitwranglers.jfrog.io/artifactory'
        ARTIFACTORY_REPO = 'docker-repo'
        ARTIFACTORY_CREDENTIALS = 'jfrog-creds'
    }

    stages {

        stage('Verify Java & Maven') {
            steps {
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
                    docker compose down || true
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
