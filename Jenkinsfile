pipeline {
    agent { label 'docker1' }

    environment {
        // Use Temurin 21 explicitly
        JAVA_HOME = '/usr/lib/jvm/temurin-21-jdk-amd64'
        PATH = "/opt/maven/bin:${JAVA_HOME}/bin:${env.PATH}"

        // Docker image info
        IMAGE_NAME = 'todo-springboot-app'
        IMAGE_TAG = "v${BUILD_NUMBER}"

        // Artifactory info
        ARTIFACTORY_URL = 'bitwranglers.jfrog.io'
        ARTIFACTORY_REPO = 'docker-repo'
    }

    stages {

        stage('Verify Java & Maven') {
            steps {
                sh '''
                    echo "JAVA_HOME=$JAVA_HOME"
                    java -version
                    mvn -version
                '''
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
                withCredentials([usernamePassword(credentialsId: 'artifactory-cred', 
                                                  usernameVariable: 'ART_USER', 
                                                  passwordVariable: 'ART_PASS')]) {
                    sh """
                        echo "Logging into Artifactory..."
                        echo $ART_PASS | docker login ${ARTIFACTORY_URL} -u $ART_USER --password-stdin

                        echo "Tagging Docker image..."
                        docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${IMAGE_NAME}:${IMAGE_TAG}

                        echo "Pushing Docker image..."
                        docker push ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${IMAGE_NAME}:${IMAGE_TAG}

                        echo "Logging out..."
                        docker logout ${ARTIFACTORY_URL}
                    """
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
