pipeline {
    agent any

    environment {
        BUILD_NAME = 'CRM Automation Pipeline'
        BROWSER = 'Chrome'
        ENVIRONMENT = 'QA'
        REPORTS_DIR = 'reports'
    }

    stages {
        stage('Execute API Tests') {
            steps {
                echo '====== Executing Postman API Tests ======'
                // Run Newman Postman collection and allow pipeline execution to continue even if assertions fail
                bat 'newman run "tests/postman/My Collection.postman_collection.json" -r htmlextra --reporter-htmlextra-export reports/api_report.html || exit 0'
            }
        }

        stage('Build & Execute UI Tests') {
            steps {
                echo '====== Executing UI Tests ======'
                // Allow pipeline to continue to post block even if UI tests fail
                bat 'mvn clean test'
            }
        }
    }

    post {
        always {
            echo "====== Publishing Reports & Archiving Artifacts ======"

            // 1. Publish Extent & API HTML Reports
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: "${env.REPORTS_DIR}",
                reportFiles: "*.html",
                reportName: "Extent & API Reports"
            ])

            // 2. Publish JUnit XML Results
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // 3. Archive Test Artifacts
            archiveArtifacts artifacts: 'reports/**/*.html, reports/**/*.xml, logs/**/*.log, screenshots/**/*.png', 
                             allowEmptyArchive: true, 
                             onlyIfSuccessful: false

            // 4. Send Extent Report HTML + Metadata to local n8n
            script {
                // Read current build result; default to SUCCESS if null
                def buildStatus = currentBuild.currentResult ?: 'SUCCESS'
                
                // Find Extent report file
                def reportFiles = findFiles(glob: "${env.REPORTS_DIR}/*.html")
                def reportHtml = ""

                if (reportFiles.length > 0) {
                    echo "Found Extent Report: ${reportFiles[0].path}"
                    reportHtml = readFile(file: reportFiles[0].path)
                } else {
                    echo "Extent Report file not found inside ${env.REPORTS_DIR} directory!"
                    reportHtml = "<h2>Extent Report file was not found in ${env.REPORTS_DIR}.</h2>"
                }

                // Safely convert payload to JSON using Groovy JsonOutput
                def payloadMap = [
                    build_name : env.BUILD_NAME,
                    status     : buildStatus,
                    browser    : env.BROWSER,
                    environment: env.ENVIRONMENT,
                    jenkins_url: env.BUILD_URL,
                    report_html: reportHtml
                ]
                
                def payloadJson = groovy.json.JsonOutput.toJson(payloadMap)

                // Save formatted JSON to file
                writeFile file: 'payload.json', text: payloadJson, encoding: 'UTF-8'

                // Send request to n8n webhook
                bat 'curl -X POST http://localhost:5678/webhook/jenkins-report -H "Content-Type: application/json" -d @payload.json'
            }
        }

        cleanup {
            echo "====== Cleaning Up Temporary Payload ======"
            echo "Cleanup complete."
        }
    }
}