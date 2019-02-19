pipeline {
    /* agent {
        docker {
            image 'maven:3-alpine'
            args '-v /Users/jianghui/.m2:/root/.m2'
        }
    } 不能使用这个，否则在部署脚本里会找不到scp命令，因为maven docker里没有这个命令*/
    agent any
    stages {
        stage('Build') {
        	agent {
                docker {
		            image 'maven:3-alpine'
		            args '-v /Users/jianghui/.m2:/root/.m2'
		        }
            }
            steps {
                sh 'mvn -B -DskipTests clean package'
                // echo '先不要打包，测试部署'
            }
        }
        stage('Deliver') { 
        	// 需要将主机的~/.ssh映射到jenkins docker的~/.ssh
            steps {
                // sh 'sudo ./jenkins/scripts/deliver.sh' 
                sh './jenkins/scripts/deliver.sh'
                // sh 	'''
                // 	echo "准备执行部署脚本"
                // 	sudo scp ./target/allinone-0.0.1-SNAPSHOT.jar root@193.112.47.33:/home/joshui
            	// '''
            }
        }
    }
}

/* 
pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Deliver') { 
            steps {
                sh './jenkins/scripts/deliver.sh' 
            }
        }
    }
}
 */

/* 
pipeline {
    agent any
    tools {
        maven 'apache-maven-3.0.1' 
    }
    stages {
        stage('Example') {
            steps {
                sh 'mvn --version'
            }
        }
    }
}
 */