pipeline {
    agent any

    environment {
        IMAGE_NAME = 'mr-bserver:latest'
        CONTAINER_NAME = 'MR-BServer'
        PORT = '4433'
    }

    stages {
        stage('拉取代码') {
            steps {
                git branch: 'master',
                    url: 'https://gh.llkk.cc/https://github.com/MalayRental/MalayRental-Server-Spring.git',
                    credentialsId: 'github-token'
            }
        }
        stage('Maven 构建') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('构建 Docker 镜像') {
            steps {
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }
        stage('部署 Docker 容器') {
            steps {
                sh """
                docker stop ${CONTAINER_NAME} || true
                docker rm ${CONTAINER_NAME} || true
                docker run -d --name ${CONTAINER_NAME} -p ${PORT}:${PORT} \
                    -v /root/DataPart/ServerImages:/data/ServerImages:rw \
                    -v /root/DataPart/SSL:/data/SSL:rw \
                    ${IMAGE_NAME}
                """
            }
        }
    }
} 