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
                git branch: 'main',
                    url: 'https://github.com/kiranmai-117/spring-todo.git'
            }
        }

        stage('Collect Code Files') {
            steps {
                script {
                    // Find files safely using shell
                    def files = sh(
                        script: "find . -type f \\( -name '*.java' -o -name '*.gradle' -o -name '*.yml' \\) ! -path '*/build/*'",
                        returnStdout: true
                    ).trim().split("\n")

                    // SAFETY: avoid Groovy unsafe methods like take(), substring(), etc.
                    def selectedFiles = []

                    int limit = 10
                    int count = 0

                    for (f in files) {
                        if (f?.trim()) {
                            selectedFiles.add(f.trim())
                            count++
                            if (count >= limit) {
                                break
                            }
                        }
                    }

                    env.FILE_LIST = selectedFiles.join(",")
                    echo "Files collected: ${env.FILE_LIST}"
                }
            }
        }

        stage('Read Files Content') {
            steps {
                script {
                    def reviewData = ""

                    def fileArray = env.FILE_LIST.split(",")

                    for (filePath in fileArray) {
                        if (filePath?.trim()) {
                            def content = readFile(filePath.trim())

                            // SAFE truncation (NO take(), NO substring Groovy calls)
                            int maxLen = 500
                            if (content.length() > maxLen) {
                                content = content.substring(0, maxLen)
                            }

                            reviewData += "\n\n===== ${filePath} =====\n"
                            reviewData += content
                        }
                    }

                    env.CODE_FOR_REVIEW = reviewData
                }
            }
        }

        stage('AI Code Review (Ollama)') {
            steps {
                script {
                    echo "Sending code to AI review engine..."

                    // Example placeholder for Ollama API call
                    sh """
                        curl -s http://localhost:11434/api/generate \
                        -d '{
                            "model": "llama3",
                            "prompt": "Review this code:\\n${env.CODE_FOR_REVIEW}",
                            "stream": false
                        }'
                    """
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline finished"
        }
    }
}