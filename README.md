# fisa240220_2
목표 ci/ cd 학습
사용 툴: genkins, ngrok(localhost인 젠킨스랑 연결)

# ci의 과정 

ci의 방법
젠킨스에서 파이프라인에 
  stage('github clone') {
            steps {
                git credentialsId: 'credentialsId', url: '깃주소'
            }
        }
        stage('build'){
                steps{
                    dir(''){
                        sh'''
                            echo build start
                            ./gradlew clean bootJar
                        '''
                    }
                }
            }
            스테이지 추가해 준다. 
