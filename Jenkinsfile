pipeline {
    agent any

    tools {
        maven 'Maven 3.8.8' // Ensure this matches your configured Maven tool name in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean test'
            }
        }
    }

    post {
        always {
            script {
                // 1. Define the relative path to your Extent HTML Report
                def reportPath = "Extent_Reports/TestReport_*.html" 
                
                // Search for the generated report file dynamically
                def reportFile = findFiles(glob: '**/Extent_Reports/*.html')
                def reportHtml = ""

                if (reportFile.length > 0) {
                    echo "Found Extent Report: ${reportFile[0].path}"
                    reportHtml = readFile(file: reportFile[0].path)
                    
                    // Escape special characters to prevent cURL JSON payload corruption
                    reportHtml = reportHtml
                        .replace('\\', '\\\\')
                        .replace('"', '\\"')
                        .replace('\r', '')
                        .replace('\n', '\\n')
                        .replace('\t', '\\t')
                } else {
                    echo "Extent Report file not found!"
                    reportHtml = "<h2>Extent Report file was not found on the agent.</h2>"
                }

                // Construct JSON payload
                def payload = """{
                    "build_name": "${env.JOB_NAME}",
                    "status": "${currentBuild.currentResult}",
                    "browser": "Chrome",
                    "environment": "QA",
                    "jenkins_url": "${env.BUILD_URL}",
                    "report_html": "${reportHtml}"
                }"""

                // Write payload to a temporary file to prevent Windows BAT command-line length limits
                writeFile file: 'payload.json', text: payload, encoding: 'UTF-8'

                // Send POST request to n8n webhook
                bat 'curl -X POST http://localhost:5678/webhook/jenkins-report -H "Content-Type: application/json" -d @payload.json'
            }
        }
    }
}