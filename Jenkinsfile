pipeline {

    agent any

    tools {
        jdk 'JDK17'
    }

    stages {

        stage('Build') {
            steps {
                echo 'Build started'
            }
        }
    }

    post {
        success {
            echo 'Build Successful'
        }

        failure {
            echo 'Build Failed'
        }
    }
}
