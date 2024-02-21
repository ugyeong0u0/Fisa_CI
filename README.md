# fisa240220_2
목표 ci/ cd 학습
사용 툴: docker(genkins 컨테이너 구동), genkins, ngrok(localhost인 젠킨스랑 연결)

# 환경셋팅
도커 설치 이후부터 

1. genkins 이미지 다운로드 및 container 실행 

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



**결과**

![캡처](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/f9ce6d13-d1a0-4b8a-acba-91987942ec23)


                }
            }
            스테이지 추가해 준다. 
