pipeline {
    agent { dockerfile true }

    environment {
        SPRING_PROFILES_ACTIVE = 'docker'
        DB_URL = 'jdbc:postgresql://db-seblog:5432/seblog'
        DB_USER = 'seblog'
        DB_PASS = 'seblogpass'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend-seblog') {
                    sh '../mvnw clean compile -Pdocker'
                }
            }
        }

        stage('Start Database') {
            steps {
                script {
                    docker.compose.up(serviceNames: 'db-seblog')
                }
            }
        }

        stage('Wait for DB') {
            steps {
                sh '''
                until pg_isready -h localhost -p 5433 -U seblog; do
                    echo "Ожидание PostgreSQL..."
                    sleep 5
                done
                '''
            }
        }

        stage('Test & Package Backend') {
            steps {
                dir('backend-seblog') {
                    sh '''
                    ../mvnw package -Pdocker \
                        -Dspring.datasource.url=${DB_URL} \
                        -Dspring.datasource.username=${DB_USER} \
                        -Dspring.datasource.password=${DB_PASS} \
                        -Dspring.jpa.hibernate.ddl-auto=validate
                    '''
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend-seblog') {
                    sh 'npm install'
                    sh 'npm run build -- --configuration=production'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker-compose build backend-seblog frontend-seblog'
            }
        }

        stage('Run Integration Tests (Optional)') {
            steps {
                sh 'echo "Интеграционные тесты пока не реализованы"'
            }
        }
    }

    post {
        always {
            sh 'docker-compose down'
        }
        success {
            echo '✅ Сборка и тесты пройдены.'
        }
        failure {
            echo '❌ Ошибка в CI. Проверь логи.'
        }
    }
}