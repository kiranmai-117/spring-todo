// pipeline {
//     agent any
//
//     tools {
//         jdk 'jdk25'
//     }
//
//     environment {
//         JAVA_HOME = tool 'jdk25'
//         PATH = "${JAVA_HOME}/bin:${env.PATH}"
//         OLLAMA_URL = "http://localhost:11434/api/generate"
//         MODEL = "llama3"
//     }
//
//     stages {
//
//         stage('Checkout') {
//             steps {
//                 git url: 'git@github.com:kiranmai-117/spring-todo.git', branch: 'main'
//             }
//         }
//
//         stage('Build') {
//             steps {
//                 sh './gradlew clean build'
//             }
//         }
//
//         stage('Collect Code Snapshot') {
//             steps {
//                 script {
//                     // Collect key project files for review
//                     sh '''
//                     find . -type f \\( -name "*.java" -o -name "*.kt" -o -name "*.gradle" \\) \
//                     ! -path "*/build/*" ! -path "*/.gradle/*" > code_files.txt
//                     '''
//
//                     def files = readFile('code_files.txt').split("\n")
//                     def content = ""
//
//                     for (f in files.take(30)) {  // limit to avoid huge payload
//                         if (f?.trim()) {
//                             content += "\n\n===== FILE: ${f} =====\n"
//                             content += sh(script: "cat ${f} || true", returnStdout: true)
//                         }
//                     }
//
//                     writeFile file: 'repo_snapshot.txt', text: content
//                 }
//             }
//         }
//
//         stage('AI Code Review (Ollama)') {
//             steps {
//                 script {
//                     def repoContent = readFile('repo_snapshot.txt')
//
//                     def prompt = """
// You are a senior Java Spring Boot reviewer.
//
// Review this code for:
// - Java 25 compatibility issues
// - Spring Boot best practices
// - Gradle issues
// - security issues
// - performance problems
// - bad coding patterns
//
// Give structured feedback with file-level suggestions.
//
// CODE:
// ${repoContent.take(12000)}
// """
//
//                     def response = sh(
//                         script: """
//                         curl -s ${OLLAMA_URL} -d '{
//                           "model": "${MODEL}",
//                           "prompt": "${prompt.replace("\"", "\\\"")}",
//                           "stream": false
//                         }'
//                         """,
//                         returnStdout: true
//                     )
//
//                     echo "===== AI CODE REVIEW ====="
//                     echo response
//                 }
//             }
//         }
//     }
//
//     post {
//         always {
//             archiveArtifacts artifacts: 'repo_snapshot.txt', allowEmptyArchive: true
//         }
//     }
// }
//


pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                git url: 'https://github.com/kiranmai-117/spring-todo.git', branch: 'main'
            }
        }

        stage('Collect Code') {
            steps {
                sh '''
                find . -type f \\( -name "*.java" -o -name "*.gradle" -o -name "*.yml" \\) \
                ! -path "*/build/*" > files.txt
                '''

                script {
                    def files = readFile('files.txt').split("\n")
                    def content = ""

                    for (f in files.take(50)) {
                        if (f?.trim()) {
                            content += "\n\n===== FILE: ${f} =====\n"
                            content += sh(script: "cat ${f} || true", returnStdout: true)
                        }
                    }

                    writeFile file: 'code.txt', text: content
                }
            }
        }

        stage('AI Code Review (Ollama)') {
            steps {
                script {
                    def code = readFile('code.txt')

                    def prompt = """
You are a senior Java Spring Boot reviewer.

Review this project for:
- code quality issues
- Spring Boot best practices
- security issues
- performance problems
- Gradle issues

Give actionable feedback per file.

CODE:
${code.substring(0, Math.min(code.length(), 12000))}
"""

                    sh """
                    curl -s http://localhost:11434/api/generate -d '{
                      "model": "llama3",
                      "prompt": "${prompt.replace('"','\\"')}",
                      "stream": false
                    }'
                    """
                }
            }
        }
    }
}
