pipeline {
    gent { label 'built-in' } 

    environment {
        
        IMAGE_NAME = 'todo-springboot-app'             // Any name you want for your image
        IMAGE_TAG = "v${BUILD_NUMBER}"                 // Auto versioning
        // Maven and JDK paths (optional if Jenkins already has them configured globally)
        JAVA_HOME = '/usr/lib/jvm/temurin-21-jdk-amd64'
        PATH = "${JAVA_HOME}/bin:/usr/share/maven/bin:${PATH}"

        // Artifactory configuration
        ARTIFACTORY_SERVER_ID = 'bitwranglers'        // any name you like
        ARTIFACTORY_URL = 'https://bitwranglers.jfrog.io/artifactory'
        ARTIFACTORY_REPO = 'docker-repo'                    // replace with your actual Maven repo key
        ARTIFACTORY_CREDENTIALS = 'jfrog-creds'       // Jenkins credentials ID for JFrog user/pass





    }

    stages {

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
                    def server = Artifactory.server(JFROG_SERVER_ID)
                    def rtDocker = Artifactory.docker(server: server)

                    rtDocker.push(
                        "${IMAGE_NAME}:${IMAGE_TAG}",
                        DOCKER_REPO
                    )
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
