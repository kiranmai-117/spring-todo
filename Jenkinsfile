// pipeline {
//     agent any
//
//     environment {
//         GIT_REPO = 'https://github.com/kiranmai-117/spring-todo.git'
//     }
//
//     stages {
//
//         stage('Checkout') {
//             steps {
//                 git branch: 'main', url: "${GIT_REPO}"
//             }
//         }
//
//         stage('Collect Code Files') {
//             steps {
//                 script {
//                     def files = sh(
//                         script: "find . -type f \\( -name '*.java' -o -name '*.gradle' -o -name '*.yml' \\) ! -path '*/build/*'",
//                         returnStdout: true
//                     ).trim().split("\n")
//
//                     def codebase = ""
//
//                     for (f in files) {
//                         if (f?.trim()) {
//                             def content = readFile(f)
//
//                             // SAFE trimming (no Groovy take(), no unsafe methods)
//                             if (content.length() > 3000) {
//                                 content = content.substring(0, 3000)
//                             }
//
//                             codebase += "\n\n===== FILE: ${f} =====\n${content}"
//                         }
//                     }
//
//                     env.CODE_FOR_REVIEW = codebase
//                 }
//             }
//         }
//
//         stage('AI Code Review (Ollama)') {
//             steps {
//                 script {
//                     def payload = [
//                         model: "llama3.2:latest",
//                         prompt: """
//                         You are a senior software engineer.
//                         Review this code and suggest improvements:
//
//                         ${env.CODE_FOR_REVIEW}
//                         """.stripIndent(),
//                         stream: false
//                     ]
//
//                     def json = groovy.json.JsonOutput.toJson(payload)
//
//                     writeFile file: 'request.json', text: json
//
//                     def response = sh(
//                         script: """
//                         curl -s http://localhost:11434/api/generate \
//                         -H "Content-Type: application/json" \
//                         -d @request.json
//                         """,
//                         returnStdout: true
//                     ).trim()
//
//                     writeFile file: 'review.txt', text: response
//                     echo "AI Review completed"
//                     echo "${response.response}"
// //                     sh "cat review.txt"
//                 }
//             }
//         }
//     }
//
//     post {
//         always {
//             archiveArtifacts artifacts: 'review.txt', onlyIfSuccessful: false
//         }
//     }
// }

pipeline {
    agent any

    environment {
        MODEL = "ollama/qwen3:1.7b"
        PATH = "/opt/homebrew/bin:/usr/local/bin:${env.PATH}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-org/your-repo.git'
            }
        }

        stage('Verify Tools') {
            steps {
                sh '''
                echo "Checking OpenCode..."
                opencode --version || exit 1

                echo "Checking Ollama models..."
                ollama list
                '''
            }
        }

        stage('AI Code Analysis (Qwen3)') {
            steps {
                sh '''
                opencode \
                  --prompt "hello" \
                  > opencode_report.txt
                '''
            }
        }

        stage('Show Report') {
            steps {
                sh '''
                echo "===== Qwen3 OpenCode Report ====="
                cat opencode_report.txt
                '''
            }
        }

        stage('Archive Report') {
            steps {
                archiveArtifacts artifacts: 'opencode_report.txt', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "Qwen3 AI analysis completed successfully"
        }

        failure {
            echo "Pipeline failed during OpenCode execution"
        }
    }
}