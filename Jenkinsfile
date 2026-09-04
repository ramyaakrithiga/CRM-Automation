/**
 * Jenkins Pipeline for CRM Automation Testing
 * Formatted specifically for Windows Jenkins Node  (using batch commands)
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
        POSTMAN_DIR = "tests/postman"
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
                    REM Use clean test so artifacts are fresh and updated config files are picked up
                    mvn clean test ^
                        -Dbrowser=${params.BROWSER} ^
                        -Denvironment=${params.ENVIRONMENT} ^
                        -Dheadless=${params.HEADLESS} ^
                        -DthreadCount=${params.THREAD_COUNT}

                    REM Debug: list surefire-reports to verify XMLs exist
                    echo Listing target\\surefire-reports
                    if exist target\\surefire-reports (
                        dir target\\surefire-reports /B
                    ) else (
                        echo target\\surefire-reports not found
                    )

                    echo Listing target\\surefire-reports\\junitreports
                    if exist target\\surefire-reports\\junitreports (
                        dir target\\surefire-reports\\junitreports /B
                    ) else (
                        echo target\\surefire-reports\\junitreports not found
                    )

                    REM Show if Python is available
                    echo Checking for Python
                    where python || echo Python not found

                    REM Generate per-test HTML reports from surefire junit XMLs (requires Python on agent)
                    if exist target\\surefire-reports\\junitreports (
                        if not exist "reports\\per_test" mkdir "reports\\per_test"
                        for %%f in (target\\surefire-reports\\junitreports\\*.xml) do (
                            echo Running per-test generator for %%f
                            python "scripts\\generate_per_test_reports.py" -i "%%f" -o "reports\\per_test" || echo Failed to run generator for %%f
                        )
                    ) else (
                        echo Skipping per-test generation: no junitreports found
                    )
                """
            }
        }

        stage('Execute Postman Tests') {
            steps {
                echo "====== Running Postman API Collection via Newman ======"
                bat '''
                    @echo off
                    if not exist "%REPORTS_DIR%" mkdir "%REPORTS_DIR%"

                    REM Add global npm path dynamically to environment PATH for this session
                    set "PATH=%APPDATA%\\npm;%ProgramFiles%\\nodejs;%PATH%"

                    REM Verify Postman folder exists
                    if not exist "%POSTMAN_DIR%" (
                        echo ERROR: Directory %POSTMAN_DIR% does not exist in repository!
                        exit /b 0
                    )

                    REM Execute all postman collections found in tests/postman using npx
                    for %%f in ("%POSTMAN_DIR%\\*.json") do (
                        echo Executing Postman Collection: %%f
                        call npx --yes newman run "%%f" ^
                            --reporters cli,junit,htmlextra ^
                            --reporter-junit-export "%REPORTS_DIR%\\newman-report-%%~nf.xml" ^
                            --reporter-htmlextra-export "%REPORTS_DIR%\\newman-report-%%~nf.html"
                    )
                '''
            }
        }
    }

    post {
        always {
            echo "====== Publishing Reports & Archiving Artifacts ======"

            // 1. Publish Extent / HTML Reports & Newman HTML extra reports
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: "${env.REPORTS_DIR}",
                reportFiles: "*.html",
                reportName: "Extent & API Reports"
            ])

            // 1b. Publish per-test HTML reports generated by the pipeline
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: "${env.REPORTS_DIR}/per_test",
                reportFiles: "**/*.html",
                reportName: "Per-test Reports"
            ])

            // Debug: list per-test reports so console shows whether files exist
            bat '''
                echo Listing per-test reports directory
                if exist "reports\\per_test" (
                    dir "reports\\per_test" /B
                ) else (
                    echo reports\\per_test not found
                )
            '''

            // 2. Publish JUnit XML Results (Includes Surefire and Newman XML reports)
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml, reports/newman-report-*.xml'

            // 3. Archive Test Artifacts (Reports, Logs, Screenshots, XMLs)
            archiveArtifacts artifacts: 'reports/**/*.html, reports/**/*.xml, logs/**/*.log, screenshots/**/*.png', 
                             allowEmptyArchive: true, 
                             onlyIfSuccessful: false

            // 4. Send Webhook Data to n8n via Windows Batch Curl
            script {
                def buildStatus = currentBuild.result ?: 'SUCCESS'
                
                bat """
                    curl -X POST ^
                    -H "Content-Type: application/json" ^
                    -d "{\\\"build_name\\\": \\"${env.BUILD_NAME}\\\", \\\"status\\\": \\"${buildStatus}\\\", \\\"duration\\\": \\"${currentBuild.durationString}\\\", \\\"browser\\\": \\"${params.BROWSER}\\\", \\\"environment\\\": \\"${params.ENVIRONMENT}\\\", \\\"jenkins_url\\\": \\"${env.BUILD_URL}\\\"}" ^
                    https://enigmatic-marxism-spearfish.ngrok-free.dev/webhook/jenkins-report
                """
            }
        }

        cleanup {
            echo "====== Cleaning Workspace ======"
            deleteDir()
        }
    }
}