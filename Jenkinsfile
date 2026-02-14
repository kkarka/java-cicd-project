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
            sh 'docker build -t kkarka/java-cicd-app:${BUILD_NUMBER} .'
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
                docker push kkarka/java-cicd-app:${BUILD_NUMBER}
                '''
            }
        }
    }

    stage('Update Helm Chart') {
       stage('Update Helm Repo') {
            steps {
                sh '''
                rm -rf helm-repo

                git clone git@github.com:kkarka/springboot-manifests.git helm-repo

                cd helm-repo/helm/springboot-app
                # Update image repository (optional) 
                sed -i "s|repository:.*|repository: kkarka/java-cicd-app|" values.yaml
                
                sed -i "s|tag:.*|tag: ${BUILD_NUMBER}|" values.yaml

                cd ../../..

                git config user.email "jenkins@local"
                git config user.name "Jenkins"

                git add values.yaml
                git commit -m "Update image tag to ${BUILD_NUMBER}"
                git push
                '''    
            }
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

