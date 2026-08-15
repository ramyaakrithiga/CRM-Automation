/**
 * Jenkins Pipeline for CRM Automation Testing
 * Formatted specifically for Windows Jenkins Node (using batch commands)
 */

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 1, unit: 'HOURS')
        timestamps()
    }

    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Select browser for testing'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'production'],
            description: 'Select target environment'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run tests in headless mode'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '1',
            description: 'Number of parallel execution threads'
        )
    }

    environment {
        BUILD_NAME = "CRM-Automation-${BUILD_NUMBER}"
        REPORTS_DIR = "reports"
        LOGS_DIR = "logs"
        SCREENSHOTS_DIR = "screenshots"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "====== Checking out source code ======"
                checkout scm
                echo "✓ Code checkout completed"
            }
        }

        stage('Build & Setup') {
            steps {
                echo "====== Setting up test environment ======"
                bat """
                    java -version
                    mvn -version
                    if not exist "${env.REPORTS_DIR}" mkdir "${env.REPORTS_DIR}"
                    if not exist "${env.LOGS_DIR}" mkdir "${env.LOGS_DIR}"
                    if not exist "${env.SCREENSHOTS_DIR}" mkdir "${env.SCREENSHOTS_DIR}"
                    mvn clean compile -q
                """
            }
        }

        stage('Execute Tests') {
            steps {
                echo "====== Running automation tests ======"
                bat """
                    if not exist "${env.REPORTS_DIR}" mkdir "${env.REPORTS_DIR}"
                    if not exist "${env.LOGS_DIR}" mkdir "${env.LOGS_DIR}"
                    if not exist "${env.SCREENSHOTS_DIR}" mkdir "${env.SCREENSHOTS_DIR}"
                    mvn test ^
                        -Dbrowser=${params.BROWSER} ^
                        -Denvironment=${params.ENVIRONMENT} ^
                        -Dheadless=${params.HEADLESS} ^
                        -DthreadCount=${params.THREAD_COUNT}
                """
            }
        }
    }

    post {
        always {
            echo "====== Publishing Reports & Archiving Artifacts ======"

            // 1. Publish Extent / HTML Reports
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: "${env.REPORTS_DIR}",
                reportFiles: "*.html",
                reportName: "Extent Report"
            ])

            // 2. Publish JUnit / TestNG XML Results from surefire-reports
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // 3. Archive Test Artifacts (Reports, Logs, Screenshots)
            archiveArtifacts artifacts: 'reports/**/*.html, logs/**/*.log, screenshots/**/*.png', 
                             allowEmptyArchive: true, 
                             onlyIfSuccessful: false

            // 4. Send Email Notifications (Optional)
            script {
                def buildStatus = currentBuild.result ?: 'SUCCESS'
                def buildMessage = """
                    Build Name: ${env.BUILD_NAME}
                    Status: ${buildStatus}
                    Duration: ${currentBuild.durationString}
                    Browser: ${params.BROWSER}
                    Environment: ${params.ENVIRONMENT}
                    Jenkins URL: ${env.BUILD_URL}
                """

                emailext(
                    to: '${DEFAULT_RECIPIENTS}',
                    subject: "Jenkins Build ${env.BUILD_NAME} - ${buildStatus}",
                    body: buildMessage,
                    attachmentsPattern: "${env.REPORTS_DIR}/*.html"
                )
            }
        }

        cleanup {
            echo "====== Cleaning Workspace ======"
            deleteDir()
        }
    }
}