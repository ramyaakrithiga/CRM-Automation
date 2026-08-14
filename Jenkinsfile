/**
 * Jenkins Pipeline for CRM Automation Testing
 * This pipeline orchestrates the build, test, and reporting stages
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
            description: 'Select environment'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: false,
            description: 'Run tests in headless mode'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '1',
            description: 'Number of parallel threads'
        )
    }

    environment {
        // Build Information
        BUILD_NAME = "CRM-Automation-${BUILD_NUMBER}"
        WORKSPACE_PATH = "${WORKSPACE}"

        // Test Configuration
        BROWSER = "${params.BROWSER}"
        ENVIRONMENT = "${params.ENVIRONMENT}"
        HEADLESS = "${params.HEADLESS}"
        THREAD_COUNT = "${params.THREAD_COUNT}"

        // Paths
        REPORTS_DIR = "${WORKSPACE}/reports"
        LOGS_DIR = "${WORKSPACE}/logs"
        SCREENSHOTS_DIR = "${WORKSPACE}/screenshots"

        // Java Configuration
        JAVA_HOME = "/usr/lib/jvm/java-11-openjdk"
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "====== Checking out source code ======"
                    checkout scm
                    echo "✓ Code checkout completed"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "====== Building project ======"
                    sh '''
                        echo "Java Version:"
                        java -version
                        echo ""
                        echo "Maven Version:"
                        mvn -version
                        echo ""
                        echo "Building Maven project..."
                        mvn clean install -DskipTests -q
                    '''
                    echo "✓ Build completed successfully"
                }
            }
        }

        stage('Pre-Test Setup') {
            steps {
                script {
                    echo "====== Setting up test environment ======"
                    sh '''
                        # Create required directories
                        mkdir -p ${REPORTS_DIR}
                        mkdir -p ${LOGS_DIR}
                        mkdir -p ${SCREENSHOTS_DIR}

                        # Display configuration
                        echo "Test Configuration:"
                        echo "  Browser: ${BROWSER}"
                        echo "  Environment: ${ENVIRONMENT}"
                        echo "  Headless: ${HEADLESS}"
                        echo "  Thread Count: ${THREAD_COUNT}"
                        echo "  Reports Directory: ${REPORTS_DIR}"
                        echo "  Logs Directory: ${LOGS_DIR}"
                    '''
                    echo "✓ Pre-test setup completed"
                }
            }
        }

        stage('Execute Tests') {
            steps {
                script {
                    echo "====== Running automation tests ======"
                    sh '''
                        # Run tests with Maven
                        mvn clean test \
                            -Dbrowser=${BROWSER} \
                            -Denvironment=${ENVIRONMENT} \
                            -Dheadless=${HEADLESS} \
                            -DthreadCount=${THREAD_COUNT} \
                            -Dtest=com.crm.automation.tests.* \
                            || echo "Some tests failed - continuing with report generation"
                    '''
                    echo "✓ Test execution completed"
                }
            }
        }

        stage('Generate Reports') {
            steps {
                script {
                    echo "====== Generating test reports ======"
                    sh '''
                        # List report files
                        if [ -d "${REPORTS_DIR}" ]; then
                            echo "Report files generated:"
                            ls -lh ${REPORTS_DIR}/*.html 2>/dev/null || echo "No HTML reports found"
                        fi

                        # Generate report summary
                        echo ""
                        echo "Test Summary:"
                        if [ -d "${LOGS_DIR}" ]; then
                            tail -50 ${LOGS_DIR}/crm-automation.log 2>/dev/null || echo "Log file not found"
                        fi
                    '''
                    echo "✓ Reports generated"
                }
            }
        }

        stage('Publish Reports') {
            steps {
                script {
                    echo "====== Publishing test reports ======"

                    // Publish HTML reports
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: "${REPORTS_DIR}",
                        reportFiles: "*.html",
                        reportName: "Extent Report"
                    ])

                    echo "✓ Reports published"
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                script {
                    echo "====== Archiving test artifacts ======"

                    // Archive reports, logs, and screenshots
                    archiveArtifacts(
                        artifacts: 'reports/**/*.html, logs/**/*.log, screenshots/**/*.png',
                        allowEmptyArchive: true,
                        onlyIfSuccessful: false
                    )

                    // Archive test results
                    junit 'target/surefire-reports/*.xml'

                    echo "✓ Artifacts archived"
                }
            }
        }

        stage('Notify Results') {
            steps {
                script {
                    echo "====== Sending notifications ======"

                    def buildStatus = currentBuild.result ?: 'SUCCESS'
                    def buildMessage = """
                        Build: ${BUILD_NAME}
                        Status: ${buildStatus}
                        Duration: ${currentBuild.durationString}
                        Browser: ${BROWSER}
                        Environment: ${ENVIRONMENT}
                        URL: ${BUILD_URL}
                    """

                    // Email notification
                    emailext(
                        to: '${DEFAULT_RECIPIENTS}',
                        subject: "Jenkins Build ${BUILD_NAME} - ${buildStatus}",
                        body: buildMessage,
                        attachmentsPattern: '${REPORTS_DIR}/*.html'
                    )

                    echo "✓ Notifications sent"
                }
            }
        }
    }

    post {
        always {
            script {
                echo "====== Pipeline Cleanup ======"
                // Clean up temporary files if needed
                cleanWs(
                    deleteDirs: true,
                    patterns: [[pattern: 'target/', type: 'INCLUDE']]
                )
            }
        }

        success {
            script {
                echo "====== Build Successful ======"
                currentBuild.result = 'SUCCESS'
            }
        }

        failure {
            script {
                echo "====== Build Failed ======"
                currentBuild.result = 'FAILURE'
            }
        }

        unstable {
            script {
                echo "====== Build Unstable ======"
                currentBuild.result = 'UNSTABLE'
            }
        }

        cleanup {
            deleteDir()
        }
    }
}

/**
 * Pipeline Description:
 *
 * 1. CHECKOUT: Clones the repository
 * 2. BUILD: Compiles the project with Maven
 * 3. PRE-TEST SETUP: Creates necessary directories and configurations
 * 4. EXECUTE TESTS: Runs automation tests with specified parameters
 * 5. GENERATE REPORTS: Creates Extent Reports and logs
 * 6. PUBLISH REPORTS: Publishes reports to Jenkins dashboard
 * 7. ARCHIVE ARTIFACTS: Archives reports, logs, and screenshots
 * 8. NOTIFY RESULTS: Sends email notifications with results
 *
 * Parameters:
 * - BROWSER: Browser selection (chrome, firefox, edge)
 * - ENVIRONMENT: Target environment (dev, staging, production)
 * - HEADLESS: Headless mode execution flag
 * - THREAD_COUNT: Number of parallel threads for test execution
 */
