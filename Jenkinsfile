pipeline {
agent any


environment {
    DOCKER_IMAGE = "kkarka/java-cicd-app:latest"
    SONAR_HOST_URL = "http://host.docker.internal:9000"
}

tools {
    maven "Maven"   // configure in Global Tool Config
}

stages {

    stage('Checkout') {
        steps {
            git branch: 'main',
                url: 'git@github.com:kkarka/java-cicd-project.git'
        }
    }

    stage('Build') {
        steps {
            sh 'mvn clean package -DskipTests'
        }
    }

    stage('SonarQube Analysis') {
        steps {
            sh """
            mvn sonar:sonar \
              -Dsonar.projectKey=java-cicd \
              -Dsonar.host.url=${SONAR_HOST_URL} \
              -Dsonar.login=sqa_f5cb36c1e68824708c2185e51873f9245a5dd17c
            """
        }
    }

    stage('Docker Build') {
        steps {
            sh 'docker build -t $DOCKER_IMAGE .'
        }
    }

    stage('Docker Push') {
        steps {
            withCredentials([usernamePassword(
                credentialsId: 'dockerhub-creds',
                usernameVariable: 'USER',
                passwordVariable: 'PASS')]) {

                sh '''
                echo $PASS | docker login -u $USER --password-stdin
                docker push $DOCKER_IMAGE
                '''
            }
        }
    }

    stage('Update Helm Chart') {
        steps {
            sh '''
            sed -i "s|repository:.*|repository: kkarka/java-cicd-app|" helm/values.yaml
            sed -i "s|tag:.*|tag: latest|" helm/values.yaml
            '''
        }
    }

    stage('Commit Changes') {
        steps {
            sh '''
            git config user.email "jenkins@example.com"
            git config user.name "jenkins"
            git add .
            git commit -m "Update image tag" || true
            git push
            '''
        }
    }
}


}

