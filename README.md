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
2023.04.27 ~ 2023.05.16
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
- Language : `java 11`
- Build Tool : `gradle`
- Framework : `spring-boot 2.7.11`
- Database : `mariaDB`
- Storage : `S3`, `cloudFront`
- CI/CD : `github action`, `code deploy`, `IAM`
- Server : `aws EC2`, `route53`, `load balancer`

<img width="400" alt="backend skillset (4)" src="https://github.com/MiniTeam-5/MiniProject-BE/assets/12468516/146b0aa8-da32-40bf-920d-bb300e2e840e">

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
<img width="5645" alt="미니프로젝트 BE (4)" src="https://github.com/MiniTeam-5/MiniProject-BE/assets/33537820/4a3c7c6e-348f-4c80-8d4b-230220117093">
</br>
</br>

## 시스템 구성도
![제목 없는 다이어그램 drawio (1)](https://github.com/MiniTeam-5/MiniProject-BE/assets/33537820/c5f538f4-2e42-40ad-9230-3aa0f9771e72)
</br>
</br>

## 개발일지
#### 서버구축
- 백엔드 프로젝트 파일 세팅 (23/04/29) - `commit` : [9d7c567](https://github.com/MiniTeam-5/MiniProject-BE/commit/9d7c567144f8bffca1a306aa9a650601beaf6038)
- deploy.yml 파일추가(github action, s3, ec2 배포) (23/04/29) - `commit` : [8b249a7](https://github.com/MiniTeam-5/MiniProject-BE/commit/8b249a7439ef0bea2a99f7fc0277dd8f13d718aa)
- appsepc.yml 파일추가 (23/04/29) - `commit` : [48ef493](https://github.com/MiniTeam-5/MiniProject-BE/commit/48ef493555a2e05548a40bd769ece8c72933afb1) 
- gh_deploy.sh 수정 (23/04/29) - `commit` : [2015bc1](https://github.com/MiniTeam-5/MiniProject-BE/commit/2015bc1393383aa1109255f424ef4f453a77ae67) 
- gh_deploy prod 모드 실행 (23/04/29) - `commit` : [612da02](https://github.com/MiniTeam-5/MiniProject-BE/commit/612da024cdadc5115495d2043d6922479db906da) 
- 스크립트 수정 (23/04/30) - `commit` : [702977a](https://github.com/MiniTeam-5/MiniProject-BE/commit/702977ab97bb09af751ed06fe86fea665e7c0037)
- run_server.sh 파일 수정 (23/04/30) - `commit` : [f8c0cb1](https://github.com/MiniTeam-5/MiniProject-BE/commit/f8c0cb10e21644da590a7ad59f443cc5d610f91b)
- AWS EC2 mariadb 연동 (23/04/30) - `commit` : [de60a4c](https://github.com/MiniTeam-5/MiniProject-BE/commit/de60a4cb661d50a8207fd27c44e4792222fb5d36)
- db접속정보보안 (23/04/30) - `commit` : [bb63227](https://github.com/MiniTeam-5/MiniProject-BE/commit/bb632277ca08c19e23b7d40501c971328df6b751)
#### API 기능구현 및 테스트
- login 기능 구현 및 완료 (23/05/02) - `commit` : [a053d54](https://github.com/MiniTeam-5/MiniProject-BE/commit/a053d544c4256391826af4b1cc69508c399cb150)
- alarm API 구현 (23/05/03) - `commit` : [b57b4cd](https://github.com/MiniTeam-5/MiniProject-BE/commit/b57b4cd2df9a882a598106040db51b58df60a450)
- 회원가입 기능 구현 완료 (23/05/03) - `commit` : [f004c71](https://github.com/MiniTeam-5/MiniProject-BE/commit/f004c7110dac6dab2d448e2a190ae7ab30a7c8f0)
- 연차/당직 신청 구현 완료 (23/05/03) - `commit` : [f700270](https://github.com/MiniTeam-5/MiniProject-BE/commit/f70027070354efe6b1c03ef92c38e6d67eb45b3e)
- 연차 당직 신청 전체 테스트 완료 (23/05/03) - `commit` : [83c0fd6](https://github.com/MiniTeam-5/MiniProject-BE/commit/83c0fd674efa24dce1a6e0264e93958db19765c6)
- alarm 기능 구현(저장, 불러오기 구현 및 테스트 완료) (23/05/03) - `commit` : [c04b5d8](https://github.com/MiniTeam-5/MiniProject-BE/commit/c04b5d8264642f82785b9bf323fd86d8d4f00e7e)
- alarm testcode 프로파일세팅, 계정등록 중복 제거 (23/05/03) - `commit` : [9f53666](https://github.com/MiniTeam-5/MiniProject-BE/commit/9f536660f9adac64dbc3de4f0e257d6aa0a9b560)
- 회원가입 시 연차 수 계산 로직 구현 (23/05/04) - `commit` : [f657254](https://github.com/MiniTeam-5/MiniProject-BE/commit/f6572544221b7c0b8dcd6bc67c5d86e08cea377f)
- 연차/당직 신청 취소 API 구현 완료 (23/05/04) - `commit` : [9d7c567](https://github.com/MiniTeam-5/MiniProject-BE/commit/6d0a6f29a379f3a0c8974f8eeb984e668654118c)
- 연차당직정보 가져오기 (23/05/04) - `commit` : [0b5bf1b](https://github.com/MiniTeam-5/MiniProject-BE/commit/0b5bf1bf02fc3e58b9740d36a5b39819829211b2)
- 연차당직월주일단위API (23/05/05) - `commit` : [b913a08](https://github.com/MiniTeam-5/MiniProject-BE/commit/b913a08ecb7640bf51826adafafe127322dc21a3)
- 매일 날짜 지난 대기 상태 연차 신청 삭제 및 유저 남은 연차 수 증가 기능 (23/05/05) - `commit` : [f9544d9](https://github.com/MiniTeam-5/MiniProject-BE/commit/f9544d9d9071cf65f237adf6026012feb369a3ec)
- 개인정보 수정 구현 (23/05/05) - `commit` : [646eba9](https://github.com/MiniTeam-5/MiniProject-BE/commit/646eba946d350bb9af56bae5e42c0366b04d28ac)
- 관리자의 연차/당직 승인 여부 결정 API 구현 완료 (23/05/05) - `commit` : [34d38b5](https://github.com/MiniTeam-5/MiniProject-BE/commit/34d38b51fe41ca0a7c6b6770d73a04642f4fbbfe)
- 연차에서 공휴일 제외 기능 추가 - 공공 API 사용 (23/05/05) - `commit` : [5169d40](https://github.com/MiniTeam-5/MiniProject-BE/commit/5169d4000ee29eee41fd812053673fd9bfe746ec)
- 프로필 수정 + 개인정보 수정 취합 (23/05/06) - `commit` : [b8e632e](https://github.com/MiniTeam-5/MiniProject-BE/commit/b8e632e54549627baaf2448d9da87e518f53e0cb)
- sseemitter 을 이용한 실시간 알람 (23/05/06) - `commit` : [9d7c567]([https://github.com/MiniTeam-5/MiniProject-BE/commit/9d7c567144f8bffca1a306aa9a650601beaf6038](https://github.com/MiniTeam-5/MiniProject-BE/commit/5b725093c12cc8548b554c759c964d1201a90930))
- 개인정보 수정 통합테스트 완료 (23/05/06) - `commit` : [6823efc](https://github.com/MiniTeam-5/MiniProject-BE/commit/6823efc3eb84e6aa1012fdf23e23d081676ec85f)
- 유저마다 입사일에 맞게 연차일수 자동 증가 기능 구현 (23/05/08) - `commit` : [5ff2c57](https://github.com/MiniTeam-5/MiniProject-BE/commit/5ff2c57c86e7fe63c0df00d99b25dba358341fc7)
- 연차/당직 수정. 리프레시 코드 테스트 (23/05/08) - `commit` : [48d8e41](https://github.com/MiniTeam-5/MiniProject-BE/commit/48d8e41e8170c3d6326be24900ac1db3cba9f746)
- 상태선택연차당직정보가져오기API,연차당직정보가져오기세달치API,특정유저연차당직정보가져오기API,모든 관리자에게 실시간 알람 전송 (23/05/09) - `commit` : [7267ffc](https://github.com/MiniTeam-5/MiniProject-BE/commit/7267ffc67962158935fa0d1ea8fa69a86f63bc53)
- s3 연동(프로필 사진 저장) (23/05/11) - `commit` : [bcdc5ff](https://github.com/MiniTeam-5/MiniProject-BE/commit/bcdc5ff45a01cfa73b48cda79ae52592ec033f64)
- 회원가입 시 기본 프로필사진 저장 (23/05/11) - `commit` : [f32ce98](https://github.com/MiniTeam-5/MiniProject-BE/commit/f32ce98fbd48994139dcf6cd899f65e0c4e115f5)
- 유저 조회 및 검색 API (23/05/12) - `commit` : [133023e](https://github.com/MiniTeam-5/MiniProject-BE/commit/133023ea3b92bd16100f33eea7147d7a8e9d4a60)
- 모든연차당직API추가 (23/05/12) - `commit` : [b253856](https://github.com/MiniTeam-5/MiniProject-BE/commit/b2538563aea2286844d6a8702cd1591253d5b3fe)
- 유저의 연차 일수 수정 API (23/05/13) - `commit` : [f6b4731](https://github.com/MiniTeam-5/MiniProject-BE/commit/f6b473193e91b1123aa59fb3e48980747bd7b732)
- excel download (23/05/14) - `commit` : [a8c1bc0](https://github.com/MiniTeam-5/MiniProject-BE/commit/a8c1bc00c76e6d16a2eabd8375d1842f20d51023)

</br>
</br>

## 📖 커밋 규약

프로젝트 [위키](https://github.com/MiniTeam-5/MiniProject-BE/wiki) 참조.

</br>
</br>

<!-- Markdown link & img dfn's -->
[wiki]: https://github.com/yourname/yourproject/wiki
