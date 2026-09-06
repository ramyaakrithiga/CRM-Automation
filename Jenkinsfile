pipeline {
    agent any

    environment {
        // Define default project variables
        BUILD_NAME = 'CRM Automation Pipeline'
        BROWSER = 'Chrome'
        ENVIRONMENT = 'QA'
        REPORTS_DIR = 'reports'
    }

    stages {
        stage('Build & Execute Tests') {
            steps {
                echo '====== Executing Tests ======'
                
                // Execute Java / Maven test suite
                bat 'mvn clean test'
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
                reportName: "Extent Reports"
            ])

            // 2. Publish JUnit XML Results (Surefire test reports)
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // 3. Archive Test Artifacts (Reports, Logs, Screenshots, XMLs)
            archiveArtifacts artifacts: 'reports/**/*.html, reports/**/*.xml, logs/**/*.log, screenshots/**/*.png', 
                             allowEmptyArchive: true, 
                             onlyIfSuccessful: false

            // 4. Send Extent Report HTML + Build Metadata to local n8n
            script {
                def buildStatus = currentBuild.result ?: 'SUCCESS'
                
                // Search for generated Extent HTML report inside your reports folder
                def reportFiles = findFiles(glob: "${env.REPORTS_DIR}/*.html")
                def reportHtml = ""

                if (reportFiles.length > 0) {
                    echo "Found Extent Report: ${reportFiles[0].path}"
                    reportHtml = readFile(file: reportFiles[0].path)
                    
                    // Escape special JSON / HTML characters safely
                    reportHtml = reportHtml
                        .replace('\\', '\\\\')
                        .replace('"', '\\"')
                        .replace('\r', '')
                        .replace('\n', '\\n')
                        .replace('\t', '\\t')
                } else {
                    echo "Extent Report file not found inside ${env.REPORTS_DIR} directory!"
                    reportHtml = "<h2>Extent Report file was not found in ${env.REPORTS_DIR}.</h2>"
                }

                // Build complete JSON payload containing full Extent Report HTML content
                def payload = """{
                    "build_name": "${env.BUILD_NAME}",
                    "status": "${buildStatus}",
                    "browser": "${env.BROWSER}",
                    "environment": "${env.ENVIRONMENT}",
                    "jenkins_url": "${env.BUILD_URL}",
                    "report_html": "${reportHtml}"
                }"""

                // Write payload to payload.json to handle large HTML payloads without Windows command length limits
                writeFile file: 'payload.json', text: payload, encoding: 'UTF-8'

                // Post the JSON payload file directly to your n8n production webhook
                bat 'curl -X POST http://localhost:5678/webhook/jenkins-report -H "Content-Type: application/json" -d @payload.json'
            }
        }

        cleanup {
            echo "====== Cleaning Workspace ======"
            deleteDir()
        }
    }
}