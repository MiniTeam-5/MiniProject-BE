# :calendar: LupinTech 연차 관리 프로그램 
> 사용자들의 연차와 당직 요청을 관리하고, 관리자가 승인 또는 거절할 수 있는 시스템을 구축한 백엔드 프로젝트입니다.

![Generic badge](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Generic badge](https://img.shields.io/github/issues-pr-closed/MiniTeam-5/MiniProject-BE)
![Generic badge](https://img.shields.io/github/license/MiniTeam-5/MiniProject-BE)


<p align="center">
<img width="1000" alt="로그인+회원가입" src="https://github.com/MiniTeam-5/MiniProject-BE/assets/33537820/d090d180-f131-4b57-bbff-206a28ee57b2">

</p>
<p align="center">
<img width="1000" alt="메인+어드민" src="https://github.com/MiniTeam-5/MiniProject-BE/assets/33537820/aaa3ad04-f7d5-479a-b1b8-dbefb6d4409e">
</p>

## :paperclip:개발 기간
2023.05.02 ~ 2023.05.16
</br>
</br>
</br>

## :family: 멤버 구성
- <b>임진묵(팀장)</b> : 연차 당직 조회 API, SSE 실시간 알람 기능, 리프레시 토큰, 서버 구축, 배포 자동화
- <b>김지수(https://github.com/CHITSOO)</b> : 연차 당직 신청, 취소, 결정 API, 관리자 권한 API, 액셀 다운로드 API, S3연동
- <b>변창우(https://github.com/wuchangb)</b> : 회원 가입, 로그인, 개인정보 조회 및 수정
</br>
</br>

## :computer: 개발 환경
### Languages and Tools
 - pigma <a href="https://www.figma.com/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/figma/figma-icon.svg" alt="figma" width="15" height="15"/> </a>
 - java 11 <a href="https://www.java.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="15" height="15"/></a>
 - gradle
 - spring-boot2.7.11  </a> <a href="https://spring.io/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" alt="spring" width="15" height="15"/></a>
 - mariaDB <a href="https://mariadb.org/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/mariadb/mariadb-icon.svg" alt="mariadb" width="15" height="15"/></a>
 - S3
 - github action
 - aws EC2 <a href="https://aws.amazon.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/amazonwebservices/amazonwebservices-original-wordmark.svg" alt="aws" width="20" height="20"/> </a>

</br>

### dependencies
```
    implementation 'org.springframework.boot:spring-boot-starter-aop'
    implementation group: 'com.auth0', name: 'java-jwt', version: '4.3.0'
    implementation group: 'org.qlrm', name: 'qlrm', version: '2.1.1'
    implementation 'com.amazonaws:aws-java-sdk-s3:1.12.232'
    implementation 'software.amazon.awssdk:s3:2.20.32'
    implementation group: 'org.springframework.cloud', name: 'spring-cloud-aws', version: '2.2.6.RELEASE', ext: 'pom'
    implementation group: 'org.apache.poi', name: 'poi', version: '5.0.0' 
    implementation group: 'org.apache.poi', name: 'poi-ooxml', version: '5.0.0'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.mariadb.jdbc:mariadb-java-client:3.1.2'
    testImplementation 'junit:junit:4.13.1'
    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    runtimeOnly 'com.h2database:h2'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'
```

</br>
</br>

## :bookmark_tabs: 주요기능
### [백엔드 기능명세](https://documenter.getpostman.com/view/12462798/UVC3kTUu)
### [API RestDocs](https://deeply-case-3b9.notion.site/API-b8599f574f74494ba79a5aa3001e9fbc)

</br>
</br>

## :mega: 사용법
```sh
$ ./gradlew clean build
```
### [:office:Lupintech.netlify.app:office:](https://lupintech.netlify.app/)

</br>
</br>

## ERD

</br>
</br>

## 시스템 구성

</br>
</br>

## 개발일지

</br>
</br>

## 📖 커밋 규약

프로젝트 [위키](https://github.com/create-go-app/cli/wiki](https://github.com/MiniTeam-5/MiniProject-BE/wiki) 참조.


## 기여 방법

1. ([https://github.com/yourname/yourproject/fork](https://github.com/MiniTeam-5/MiniProject-BE))을 포크합니다.
2. (`git checkout -b feature/fooBar`) 명령어로 새 브랜치를 만드세요.
3. (`git commit -am 'Add some fooBar'`) 명령어로 커밋하세요.
4. (`git push origin feature/fooBar`) 명령어로 브랜치에 푸시하세요. 
5. 풀리퀘스트를 보내주세요.

<!-- Markdown link & img dfn's -->
[wiki]: https://github.com/yourname/yourproject/wiki
