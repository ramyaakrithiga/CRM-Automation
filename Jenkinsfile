/**
 * Jenkins Pipeline for CRM Automation Testing
 * Optimized for automated Git triggers and reporting resilience
 */

pipeline {
    agent any

    tools {
        // Configured in Manage Jenkins -> Global Tool Configuration
        // Remove or adjust name to match your Jenkins JDK tool setup
        jdk 'JDK11'
    }

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
            description: 'Select environment'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true, // Set to true for headless automated runs on Git push
            description: 'Run tests in headless mode'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '1',
            description: 'Number of parallel threads'
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
                sh '''
                    java -version
                    mvn -version

                    # Create required workspace directories
                    mkdir -p ${REPORTS_DIR} ${LOGS_DIR} ${SCREENSHOTS_DIR}

                    # Compile test code skipping test execution
                    mvn clean compile -q
                '''
            }
        }

        stage('Execute Tests') {
            steps {
                echo "====== Running automation tests ======"
                sh """
                    mvn test \
                        -Dbrowser=${params.BROWSER} \
                        -Denvironment=${params.ENVIRONMENT} \
                        -Dheadless=${params.HEADLESS} \
                        -DthreadCount=${params.THREAD_COUNT} \
                        -Dtest=com.crm.automation.tests.*
                """
            }
        }
    }

    post {
        always {
            echo "====== Publishing Reports & Archiving ======"

            // 1. Publish Extent HTML Reports
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: "${env.REPORTS_DIR}",
                reportFiles: "*.html",
                reportName: "Extent Report"
            ])

            // 2. Publish JUnit XML Results
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // 3. Archive Artifacts
            archiveArtifacts artifacts: 'reports/**/*.html, logs/**/*.log, screenshots/**/*.png', 
                             allowEmptyArchive: true, 
                             onlyIfSuccessful: false

            // 4. Send Email Notification
            script {
                def buildStatus = currentBuild.result ?: 'SUCCESS'
                def buildMessage = """
                    Build: ${BUILD_NAME}
                    Status: ${buildStatus}
                    Duration: ${currentBuild.durationString}
                    Browser: ${params.BROWSER}
                    Environment: ${params.ENVIRONMENT}
                    URL: ${BUILD_URL}
                """

                emailext(
                    to: '${DEFAULT_RECIPIENTS}',
                    subject: "Jenkins Build ${BUILD_NAME} - ${buildStatus}",
                    body: buildMessage,
                    attachmentsPattern: "${env.REPORTS_DIR}/*.html"
                )
            }
        }

        cleanup {
            echo "====== Workspace Cleanup ======"
            deleteDir()
        }
    }
}