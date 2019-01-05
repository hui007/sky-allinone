pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Hello World。李涵还在写总结"'
                sh 'echo "Hello World"'
                sh '''
                    echo "Multiline shell steps works too"
                    ls -lah
                '''
            }
        }
    }
}