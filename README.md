# fisa240220_2
목표 : ci + aws를 이용한 무중단 배포 
목차 : 1. 로컬에서 ci 2. aws를 이용한 무중단배포(ci)
---------

## 1. ngrok를 이용해 ci 실습  
사용 툴: virtualbox, mobaxterm, docker(genkins 컨테이너 구동), genkins, ngrok(localhost인 젠킨스랑 연결)

### 과정 
1. 젠킨스랑 ngrok 포트 연결 (포트포워딩)
2. 깃에 spring boot app 푸시 후 gradlew에 실행 권한 주기
3. 깃 훅 연결(ngrok 페이로드 url에 넣기)
4. 깃에서코드 변경시 젠킨스에서 자동 빌드

gitbash에서 깃 푸시는 진행했다고 가정한다. (master가 아닌 이름 main으로 변경까지)

### 1. 젠킨스랑 ngrok 포트 연결

대제목 2. aws로 무중단배포하기에선 도커부터 다 깔지만 이번엔 docker가 깔려있다는 가정하에 진행한다. 

virtualbox 네트워크 설정에서 포트포워딩을 해준다. 
http://ngrok주소 : 8080으로 접속할 예정이라 
![image (2)](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/c817cc8f-90e4-415f-a2dd-8a8021b3467d)


1. 도커에서 genkins 이미지 다운로드 및 container 실행
```
 $ docker run --name myjenkins --privileged -p 8080:8080 jenkins/jenkins:lts-jdk17
```
2. ngrok 설치 윈도우에 받고 실행
   https://ngrok.com/ 사이트에서
   ![image (4)](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/81e6523d-d148-4ef2-9e8e-2295fdb24650)
    2번 3번 붙여넣기
   만약 실행시 세션 만료 등으로 발생되는 문제가 나타날 경우
   - 해결책 : yml 파일 삭제
   **C:\Users\사용자이름\AppData\Local\ngrok - 해당 경로에서 ngrok.yml 파일 삭제**
3. 앞에서 포트포워딩 해줬기 때문에 ngrok도 8080 포트가 맞다 
4.   ![Untitled](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/bcea57c0-cd8a-44ba-bee7-d59158ccbf42)

### 2. 깃에 spring boot app 푸시 후 gradleW에 실행 권한 주기
이유:
    ⭐⭐⭐
    gradlew 파일의 권한 644 즉 소유자만 실행 가능하기 때문에 
    github로 업로드 후 다른 시스템으로 다운로드 시에는
    755로 수정 또는 소유자가 처음부터 수정후 업로드 필수 
    
젠킨스 파이프 라인에서 sh ''''ls -al'''' 실행권한과 gitbash에서의 ls -al 실행권한 차이
![Untitled (1)](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/ea99c4c5-1915-47d5-b3d1-43b049e7a7a6)

gitbash에서 실행
```
Admin@1-15 MINGW64 /c/git/fisa_homework/step07_citest2 (main)
$ git ls-tree HEAD
100644 blob c2065bc26202b2d072aca3efc3d1c2efad3afcbf    .gitignore
100644 blob 7b464d0b02e305930f4cfd6c7f9b8d5099898c73    README.md
100644 blob 815f5a591bbd2230b7af774de4792fa0102c9210    build.gradle
040000 tree 55db975f04cca2f3a8faf1f93ee2fd8abff54f39    gradle
100644 blob 1aa94a4269074199e6ed2c37e8db3e0826030965    gradlew
100644 blob 93e3f59f135dd2dd498de4beb5c64338cc33beeb    gradlew.bat
100644 blob e378066d01cc686aca8e5503838c3a61a41b818a    settings.gradle
040000 tree 146503e8e750dda2fe8b97f2e4def4cd217c4343    src

Admin@1-15 MINGW64 /c/git/fisa_homework/step07_citest2 (main)
$ git update-index --add --chmod=+x gradlew

Admin@1-15 MINGW64 /c/git/fisa_homework/step07_citest2 (main)
$ git add .

Admin@1-15 MINGW64 /c/git/fisa_homework/step07_citest2 (main)
$ git commit -m "aa"
[main df2c49d] aa
 1 file changed, 0 insertions(+), 0 deletions(-)
 mode change 100644 => 100755 gradlew

Admin@1-15 MINGW64 /c/git/fisa_homework/step07_citest2 (main)
$ git push
Enumerating objects: 3, done.
Counting objects: 100% (3/3), done.
Delta compression using up to 8 threads
Compressing objects: 100% (2/2), done.
Writing objects: 100% (2/2), 216 bytes | 216.00 KiB/s, done.
Total 2 (delta 1), reused 0 (delta 0), pack-reused 0
remote: Resolving deltas: 100% (1/1), completed with 1 local object.
To https://github.com/ugyeong0u0/fisa240220_2.git
   c742719..df2c49d  main -> main

Admin@1-15 MINGW64 /c/git/fisa_homework/step07_citest2 (main)
$ git ls-tree HEAD
100644 blob c2065bc26202b2d072aca3efc3d1c2efad3afcbf    .gitignore
100644 blob 7b464d0b02e305930f4cfd6c7f9b8d5099898c73    README.md
100644 blob 815f5a591bbd2230b7af774de4792fa0102c9210    build.gradle
040000 tree 55db975f04cca2f3a8faf1f93ee2fd8abff54f39    gradle
100755 blob 1aa94a4269074199e6ed2c37e8db3e0826030965    gradlew
100644 blob 93e3f59f135dd2dd498de4beb5c64338cc33beeb    gradlew.bat
100644 blob e378066d01cc686aca8e5503838c3a61a41b818a    settings.gradle
040000 tree 146503e8e750dda2fe8b97f2e4def4cd217c4343    src

Admin@1-15 MINGW64 /c/git/fisa_homework/step07_citest2 (main)

```
-> 100755로 gradlew의 권한이 바뀐것으로 확인된다.

잊지말고 위에 코드처럼 다시 git push를 진행한다.


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

![캡처](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/b2f384a6-dc27-4191-9772-99ef607575d0)


