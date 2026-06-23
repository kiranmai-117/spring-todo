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

    environment {
        GIT_REPO = 'https://github.com/kiranmai-117/spring-todo.git'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: "${GIT_REPO}"
            }
        }

        stage('Collect Code Files') {
            steps {
                script {
                    def files = sh(
                        script: "find . -type f \\( -name '*.java' -o -name '*.gradle' -o -name '*.yml' \\) ! -path '*/build/*'",
                        returnStdout: true
                    ).trim().split("\n")

                    def codebase = ""

                    for (f in files) {
                        if (f?.trim()) {
                            def content = readFile(f)

                            // SAFE trimming (no Groovy take(), no unsafe methods)
                            if (content.length() > 3000) {
                                content = content.substring(0, 3000)
                            }

                            codebase += "\n\n===== FILE: ${f} =====\n${content}"
                        }
                    }

                    env.CODE_FOR_REVIEW = codebase
                }
            }
        }

        stage('AI Code Review (Ollama)') {
            steps {
                script {
                    def payload = [
                        model: "llama3",
                        prompt: """
                        You are a senior software engineer.
                        Review this code and suggest improvements:

                        ${env.CODE_FOR_REVIEW}
                        """.stripIndent(),
                        stream: false
                    ]

                    def json = groovy.json.JsonOutput.toJson(payload)

                    writeFile file: 'request.json', text: json

                    def response = sh(
                        script: """
                        curl -s http://localhost:11434/api/generate \
                        -H "Content-Type: application/json" \
                        -d @request.json
                        """,
                        returnStdout: true
                    ).trim()

                    writeFile file: 'review.txt', text: response
                    echo "AI Review completed"
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'review.txt', onlyIfSuccessful: false
        }
    }
}