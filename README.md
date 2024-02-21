
목표 : ci + aws를 이용한 무중단 배포

목차 : 1. 로컬에서 ci 2. aws를 이용한 무중단배포(ci)

요약 

가상머신위에 도커를 설치하고 그 위에 젠킨스 컨테이너를 띄운다. 
젠킨스는 localhost로 실행되기 때문에 깃허브 레포 변화에 따른 build를 재생성하려면 기본적으로 외부(깃허브)에서 변화를 구독(알림)받을 수 있는 무언가 필요하다

이 다리역할을 대제목 1번에서는 ngtok가 해준다. 

대제목 2에선 ngrok의 역할을 aws로 대체한다. 



![제목 없음](https://github.com/ugyeong0u0/Fisa_CI/assets/120684605/314c2df1-1d27-41cd-97cd-6e346905fb2d)




---------



## 1. ngrok을 이용한 ci 실습  
사용 툴: virtualbox, mobaxterm, docker(genkins 컨테이너 구동), genkins, ngrok(localhost인 젠킨스랑 연결)

### 과정 
1. 젠킨스랑 ngrok 포트 연결 (포트포워딩)
2. 깃에 spring boot app 푸시 후 gradlew에 실행 권한 주기
3. genkins sript에 build stage 추가하기
4. 깃 훅 연결(ngrok 페이로드 url에 넣기)
5. 결과 : 깃에서코드 변경시 젠킨스에서 자동 빌드

gitbash에서 깃 푸시는 진행했다고 가정한다. (master가 아닌 이름 main으로 변경까지)

### 1. 젠킨스랑 ngrok 포트 연결

대제목 2. aws로 무중단배포하기에선 도커부터 다 깔지만 이번엔 docker가 깔려있다는 가정하에 진행한다. 

virtualbox 네트워크 설정에서 포트포워딩을 해준다. 
http://ngrok주소 : 8080으로 접속할 예정이라 
![image (2)](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/c817cc8f-90e4-415f-a2dd-8a8021b3467d)

1. ngrok 설치 윈도우에 받고 실행
   https://ngrok.com/ 사이트에서
   ![image (4)](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/81e6523d-d148-4ef2-9e8e-2295fdb24650)
    2번 3번 붙여넣기
   만약 실행시 세션 만료 등으로 발생되는 문제가 나타날 경우
   - 해결책 : yml 파일 삭제
   **C:\Users\사용자이름\AppData\Local\ngrok - 해당 경로에서 ngrok.yml 파일 삭제**
  앞에서 포트포워딩 해줬기 때문에 ngrok도 8080 포트가 맞다
2. 도커에서 genkins 이미지 다운로드 및 container 실행
```
 $ docker run --name myjenkins --privileged -p 8080:8080 jenkins/jenkins:lts-jdk17
```
3. 젠킨스 파이프라인 만들기(젠킨스
   데시보드에 접속한 후부터)

   gradle 오류가 날때가 있다고 하셔서 Gradel 8.6 버전으로 변경했다. 
![Untitled (6)](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/5f88029b-e019-466a-962a-26908949b12f)

   1) \+ 새로운 item클릭
   2) item name 작성 후 pipeline클릭
   3) cofigure 창에 들어와서
      General에서 Github project 체크 후 아까 깃허브에 푸시한 https 주소를 넣는다.
      build trigger에서 Github hook trigger for GITScm polling 체크
   4) pipeline에서 오른쪽 보면 try sample Pipelne이라고 보이는 것을 드랍다운에서 Github + Maven을 클릭한다. 그러면 스크립트에 와다다 무언가 적히는데
     ![Untitled](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/bcea57c0-cd8a-44ba-bee7-d59158ccbf42)
   5) 하단 저장하기 위에 Pipeline Syntax라고 파란색 글자를 클릭한다. 그러면 새창이 나오는데 
      왼쪽 메뉴 중 Snippet Generator(이르만 봐도 느낌이 살짝 온다.)
      Steps아래 Sample Step에서 드랍다운 중 git : Git을 클릭한다.
      그러면 Repository URL 글자가 나타나는데 박스에 아까 푸시한 레폰 http 주소를 넣어주면 된다.
      branch 이름은 위에서 master에서 main으로 변경해줬기 때문에 main으로 수정한다.
      Credentials는 Add를 눌러 깃ID 비밀번호로 로그인 해준다.
      Credentials 글자 아래 Add 아래 두개의 체크 박스는 체크된 처음 상태로 둔다.
      이제 Generate Pipeline Script 누르면
      아래 사진처럼 나오는데 이부분 그대로 복사하기 아이콘 눌러서
      ![스크린샷 2024-02-21 164756](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/6213a1a0-9a49-4fc4-bab8-edc47b946f56)
      3)번 전에 페이지로 돌아와 그대로 복붙하되 Groovy 작성 형식은 아래처럼 작성하면 된다. stage('') log 확인하기 쉬운 이름으로 작성하면 된다. 
       ![Untitled (2)](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/6ff7f170-1a9b-47a5-97ba-2b2ededa1acf)
      
       
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


### 3. genkins sript에 build stage 추가하기 

젠킨스에서 파이프라인에 
```
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
          


```

 ### 4. 깃 훅 연결(ngrok 페이로드 url에 넣기)

 깃허브-> 푸시한 레포-> 레포 settings->Webhooks-> add Webhook 후 
 페이로드 url엔 ngrok 주소 작성 후 ssl enable 체크 후 완료하기

 ⭐⭐⭐ Payload URL 끝에 /github-webhook/ 꼭 넣어야함!!
 ![Untitled (3)](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/85c5487b-e0c6-45ab-817b-d244a5d4e34d)

### 5. 깃에서코드 변경시 젠킨스에서 자동 빌드 결과 사진 

![캡처](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/f9ce6d13-d1a0-4b8a-acba-91987942ec23)


-------------


## 2. aws를 이용해 ci 실습  
사용 툴: virtualbox, mobaxterm, docker(genkins 컨테이너 구동), genkins, aws 

### aws 활용(ec2 linux)
위에서 한것과 같은 과정인데 다른 점은 aws에서 받은 ubuntu를 사용했다. 
그렇기 때문에 도커 설치 등 설치 해줄 것이 많았다. 
위에 레포를 그대로 이용하기 때문에 이미 gradlew 권한은 이미 설정되어 있으므로 넘어간다.


### 과정
1. aws 인스턴스 생성하기 
2. jenkins container 설치 활용 (설치시 port 80으로 함)
3. 깃허브와 jenkins 연동
4. 깃허브에 변경내용 push시 자동으로 webhook 기능으로 알림 정보로 ubuntu에 pull 적용 및 build

### 1. aws 인스턴스 생성하기 
+ ubuntu 22.04 이용 
![Untitled (1)](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/65518945-389b-48ac-b788-87c1918704ee)


### 2. jenkins container 설치 활용 (설치시 port 80으로 함)

1. aws ubuntu랑 연결하기
지운곳엔 aws 인스턴스 ipv4 ip 주소넣기 
aws  Ec2-> 인스턴스 하나 클릭 시 인스턴스 요약나옴

![Untitled](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/c2ad4dac-fd58-4fcf-a013-b9c3a65c6980)

deafault 로그인 : ubuntu 

2. 젠킨스에서 stage 추가해서 build하기
   대제목 1-3 코드랑 같음


3. aws ec2 ubuntu에서 docker 설치


```
sudo apt update
sudo apt install apt-transport-https ca-certificates curl software-properties-common

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"
sudo apt update
sudo apt install -y docker.io
docker --version
```


4. docker 로그인

```
sudo usermod -aG docker $USER
exit() 

$ docker login -u [ID]
````


5. 젠킨스 설치

```
docker run --name myjenkins --privileged -p 80:8080 jenkins/jenkins:lts-jdk17

```


6. 웹사이트에서 젠킨스 접속

http://ec2 dns ipv4번호:80
이때 ex2dns ipv4 번호는 aws  Ec2-> 인스턴스 하나 클릭 시 인스턴스 요약나옴 존재 



### 3. 깃허브와 jenkins 연동

http://ec2 dns ipv4번호:포트번호/github-webhook/
⭐⭐⭐ Payload URL 끝에 /github-webhook/ 꼭 넣어야함!!
   ![스크린샷 2024-02-21 185351](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/eab6bb21-a10b-48ec-aaff-3bcdbb6224c9)



### 4. 깃허브에 변경내용 push시 자동으로 webhook 기능으로 알림 정보로 ubuntu에 pull 적용 결과

아래 사진에서 지금 빌드를 누르면 빌드가 너무 오래 걸리다가 웹사이트연결이 중단되는데 이는 ec2 램 메모리가 가득 찼기 때문에 실제 디스크의 용량을 이용하여 부족한 메모리를 대체할 공간을 swap memory(스왑 공간)이라 하는데 이를 통해 해결할 수 있다.

인스턴스 재부팅 후 https://repost.aws/ko/knowledge-center/ec2-memory-swap-file를 참고하여 명령어로 해결하면 된다. 

![캡처](https://github.com/ugyeong0u0/fisa240220_2/assets/120684605/b2f384a6-dc27-4191-9772-99ef607575d0)




