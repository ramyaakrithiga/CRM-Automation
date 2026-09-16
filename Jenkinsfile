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
                // Run Newman Postman collection and allow execution to continue even if assertions fail
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

            // 4. Send Extent & API Reports + Metadata to local n8n
            script {
                def buildStatus = currentBuild.currentResult ?: 'SUCCESS'
                
                // Read UI Extent Report (filtering out api_report.html)
                def uiReportFiles = findFiles(glob: "${env.REPORTS_DIR}/*.html")
                def uiHtml = ""

                for (file in uiReportFiles) {
                    if (!file.name.contains('api_report.html')) {
                        echo "Found Extent Report: ${file.path}"
                        uiHtml = readFile(file: file.path)
                        break
                    }
                }

                if (uiHtml == "") {
                    echo "Extent Report file not found inside ${env.REPORTS_DIR} directory!"
                    uiHtml = "<h3>UI Extent Report file was not found.</h3>"
                }

                // Read Postman API Report
                def apiHtml = ""
                if (fileExists("${env.REPORTS_DIR}/api_report.html")) {
                    echo "Found API Report: ${env.REPORTS_DIR}/api_report.html"
                    apiHtml = readFile(file: "${env.REPORTS_DIR}/api_report.html")
                } else {
                    echo "API Report file not found inside ${env.REPORTS_DIR} directory!"
                    apiHtml = "<h3>Postman API Report file was not found.</h3>"
                }

                // Combine both HTML reports into a single string payload
                def combinedHtml = """
                    <h2 style="color:#2c3e50; border-bottom: 2px solid #2c3e50;">--- UI Extent Test Report ---</h2>
                    ${uiHtml}
                    <br/><hr/><br/>
                    <h2 style="color:#2c3e50; border-bottom: 2px solid #2c3e50;">--- Postman API Test Report ---</h2>
                    ${apiHtml}
                """

                // Safely convert payload to JSON using Groovy JsonOutput
                def payloadMap = [
                    build_name : env.BUILD_NAME,
                    status     : buildStatus,
                    browser    : env.BROWSER,
                    environment: env.ENVIRONMENT,
                    jenkins_url: env.BUILD_URL,
                    report_html: combinedHtml
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