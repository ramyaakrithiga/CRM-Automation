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

            // 4. Send Webhook Data directly to local n8n
            script {
                def buildStatus = currentBuild.result ?: 'SUCCESS'
                
                // Set status in env so Windows batch script reads it cleanly
                env.JOB_BUILD_STATUS = buildStatus
                
                bat '''
                    curl -X POST ^
                    -H "Content-Type: application/json" ^
                    -d "{\\"build_name\\":\\"%BUILD_NAME%\\", \\"status\\":\\"%JOB_BUILD_STATUS%\\", \\"browser\\":\\"%BROWSER%\\", \\"environment\\":\\"%ENVIRONMENT%\\", \\"jenkins_url\\":\\"%BUILD_URL%\\"}" ^
                    http://localhost:5678/webhook/jenkins-report
                '''
            }
        }

        cleanup {
            echo "====== Cleaning Workspace ======"
            deleteDir()
        }
    }
}