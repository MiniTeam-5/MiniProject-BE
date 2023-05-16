# :calendar: LupinTech 연차 관리 프로그램 BackEnd
> 사용자들의 연차와 당직 요청을 관리하고, 관리자가 승인 또는 거절할 수 있는 시스템을 구축한 백엔드 프로젝트입니다.

![Generic badge](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Generic badge](https://img.shields.io/github/issues-pr-closed/MiniTeam-5/MiniProject-BE)
<a href="https://github.com/MiniTeam-5/MiniProject-BE/blob/develop/LICENSE" target="_blank">
    ![Generic badge](https://img.shields.io/github/license/MiniTeam-5/MiniProject-BE)
</a>

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
- <b>김지수</b> : 연차 당직 신청, 취소, 결정 API, 관리자 권한 API, 액셀 다운로드 API, S3연동
- <b>변창우</b> : 회원 가입, 로그인, 개인정보 조회 및 수정
</br>
</br>

## :bookmark_tabs: 주요기능
### [백엔드 기능명세](https://deeply-case-3b9.notion.site/9a411fe4fcac46c9abb5686bd8a5520c)
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

## ERD

</br>
</br>

## 시스템 구성도

</br>
</br>

## 개발일지
- 백엔드 프로젝트 파일 세팅 (23/04/29) - `commit` : [9d7c567](https://github.com/MiniTeam-5/MiniProject-BE/commit/9d7c567144f8bffca1a306aa9a650601beaf6038)
- github action, s3, ec2 배포 (23/04/29) - `commit` : [8b249a7]
- appsepc.yml 파일추가 (23/04/29) - `commit` : 
- 배포스크립트파일 반영 (23/04/29) - `commit` : 
- 빌드작업 반영 (23/04/29) - `commit` : 
- gh_deploy.sh 수정 (23/04/29) - `commit` : 
- 배포 관련 작업 반영 (23/04/29) - `commit` : 
- gh_deploy prod 모드 실행 (23/04/29) - `commit` : 
- 개발환경작업 반영 (23/04/29) - `commit` : 
- 스크립트 수정
- run_server.sh 파일 수정
- AWS EC2 mariadb 연동
- db접속정보보안
- login 기능 구현 및 완료
- Feature/login 구현완료
- alarm API 구현
- 회원가입 기능 구현 완료
- 연차/당직 신청 구현 완료
- 연차 당직 신청 전체 테스트 완료
- alarm 기능 구현(저장, 불러오기 구현 및 테스트 완료)
- alarm testcode 프로파일세팅, 계정등록 중복 제거
- fullName 전체 삭제
- Leave에 usingDays 추가
- 회원가입 시 연차 수 계산 로직 구현
- 연차/당직 신청 취소 API 구현 완료
- 연차당직정보 가져오기
- 연차당직월주일단위API
- 매일 날짜 지난 대기 상태 연차 신청 삭제 및 유저 남은 연차 수 증가 기능
- 개인정보 수정 구현
- 관리자의 연차/당직 승인 여부 결정 API 구현 완료
- 연차에서 공휴일 제외 기능 추가 - 공공 API 사용


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
