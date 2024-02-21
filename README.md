# fisa240220_2
목표 ci/ cd 학습
사용 툴: mobaxterm, docker(genkins 컨테이너 구동), genkins, ngrok(localhost인 젠킨스랑 연결)

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


## aws 활용(ec2 linux)
1. spring boot app 실행
2. jenkins container 설치 활용, 설치시 port 80
3. 깃허브와 jenkins 연동
4. 깃허브에 변경내용 push시 자동으로 webhook 기능으로 알림 정보로 ubuntu에 pull 적용 및 build

### 과정
![Untitled](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/c2ad4dac-fd58-4fcf-a013-b9c3a65c6980)


