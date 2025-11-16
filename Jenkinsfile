pipeline {
    agent { label 'docker1' } // Replace with your agent label

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64' // system JDK 21 path
        PATH = "${JAVA_HOME}/bin:/opt/maven/bin:${env.PATH}" // Add Java & Maven to PATH
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
                sh '''
                docker build -t my-todo-app:latest .
                '''
            }
        }

        stage('Push Docker Image to Artifactory') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'jfrog-creds', passwordVariable: 'JF_PASSWORD', usernameVariable: 'JF_USER')]) {
                    sh '''
                    docker login -u $JF_USER -p $JF_PASSWORD https://bitwranglers.jfrog.io/artifactory/docker-repo
                    docker tag my-todo-app:latest bitwranglers.jfrog.io/docker-repo/my-todo-app:latest
                    docker push bitwranglers.jfrog.io/docker-repo/my-todo-app:latest
                    '''
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh '''
                docker-compose down
                docker-compose up -d
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Build and deployment succeeded!'
        }
        failure {
            echo '❌ Build failed — check the logs.'
        }
    }
}
