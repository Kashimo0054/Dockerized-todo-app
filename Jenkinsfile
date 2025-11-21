pipeline {
    agent { label 'docker1' }
    
    environment {
        JAVA_HOME         = '/usr/lib/jvm/temurin-21-jdk-amd64'
        PATH              = "/opt/maven/bin:${JAVA_HOME}/bin:${env.PATH}"
        
        IMAGE_NAME        = 'todo-springboot-app'
        IMAGE_TAG         = "v${BUILD_NUMBER}"
        IMAGE_LATEST      = 'latest'
        
        ARTIFACTORY_URL   = 'bitwranglers.jfrog.io'
        ARTIFACTORY_REPO  = 'docker-repo'
        
        SONAR_HOST_URL    = 'http://SonarQube-Docker:9000'
        SONAR_PROJECT_KEY = 'todo-with-junit'
        SONAR_LOGIN = credentials('sonarqube-token')   // store token in Jenkins credentials
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Kashimo0054/Dockerized-todo-app.git'
            }
        }

        stage('Verify Tools') {
            steps {
                sh '''
                    java -version
                    mvn -version
                    docker --version
                '''
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube-Docker') {
                    withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            mvn sonar:sonar \
                              -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                              -Dsonar.host.url=${SONAR_HOST_URL} \
                              -Dsonar.login=${SONAR_LOGIN}
                        '''
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                    docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:${IMAGE_LATEST}
                """
            }
        }

        stage('Push to Artifactory') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'artifactory-cred',
                    usernameVariable: 'ART_USER',
                    passwordVariable: 'ART_PASS'
                )]) {
                    sh """
                        echo $ART_PASS | docker login ${ARTIFACTORY_URL} -u $ART_USER --password-stdin

                        docker tag ${IMAGE_NAME}:${IMAGE_TAG} \
                            ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${IMAGE_NAME}:${IMAGE_TAG}
                        docker tag ${IMAGE_NAME}:${IMAGE_LATEST} \
                            ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${IMAGE_NAME}:${IMAGE_LATEST}

                        docker push ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${IMAGE_NAME}:${IMAGE_TAG}
                        docker push ${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${IMAGE_NAME}:${IMAGE_LATEST}

                        docker logout ${ARTIFACTORY_URL}
                    """
                }
            }
        }
    }

    post {
        success {
            echo "CI Completed Successfully!"
            echo "Image available at:"
            echo "${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${IMAGE_NAME}:${IMAGE_TAG}"
            echo "${ARTIFACTORY_URL}/${ARTIFACTORY_REPO}/${IMAGE_NAME}:latest"
        }
        failure {
            echo "CI Failed – No image was pushed"
        }
        always {
            cleanWs()  // optional: keep agents clean
        }
    }
}
