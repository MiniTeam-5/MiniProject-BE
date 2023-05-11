package kr.co.lupintech.service;

import kr.co.lupintech.core.annotation.MyLog;
import kr.co.lupintech.core.auth.jwt.MyJwtProvider;
import kr.co.lupintech.core.auth.session.MyUserDetails;
import kr.co.lupintech.core.exception.Exception400;
import kr.co.lupintech.core.exception.Exception401;
import kr.co.lupintech.core.exception.Exception500;
import kr.co.lupintech.dto.user.UserRequest;
import kr.co.lupintech.dto.user.UserResponse;
import kr.co.lupintech.model.token.RefreshTokenEntity;
import kr.co.lupintech.model.token.TokenRepository;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRepository;
import kr.co.lupintech.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Random;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final S3Service s3Service;

    private final JavaMailSender javaMailSender;

    private String ePassword = 인증번호생성();


    @MyLog
    @Transactional
    public UserResponse.JoinOutDTO 회원가입(UserRequest.JoinInDTO joinInDTO) {
        //1. 이름으로 회원조회
        Optional<User> userByName = userRepository.findByUsername(joinInDTO.getUsername());
        if (userByName.isPresent()) {
            // 이 부분이 try catch 안에 있으면 Exception500에게 제어권을 뺏긴다.
            throw new Exception400("username", "유저네임이 존재합니다");
        }
        //2. 이메일로 회원조회
        Optional<User> userByEmail = userRepository.findByEmail(joinInDTO.getEmail());
        if (userByEmail.isPresent()) {
            throw new Exception400("email", "해당 이메일 주소는 사용중입니다");
        }

        //3. 비밀번호 암호화
        String encPassword = passwordEncoder.encode(joinInDTO.getPassword()); // 60Byte
        joinInDTO.setPassword(encPassword);


        // 디비 save 되는 쪽만 try catch로 처리하자.
        try {
            User userPS = userRepository.save(joinInDTO.toEntity());
            return new UserResponse.JoinOutDTO(userPS);
        } catch (Exception e) {
            throw new Exception500("회원가입 실패 : " + e.getMessage());
        }
    }

    @MyLog
    public Pair<String, String> 로그인(UserRequest.LoginInDTO loginInDTO) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

            String accessjwt = MyJwtProvider.createAccess(myUserDetails.getUser());
            Pair<String, RefreshTokenEntity> rtInfo = MyJwtProvider.createRefresh();

            //로그인 성공하면 액세스 토큰, 리프레시 토큰 발급. 리프레시 토큰의 uuid은 DB에 저장
            tokenRepository.save(rtInfo.getSecond());

            return Pair.of(accessjwt, rtInfo.getFirst());
        } catch (Exception e) {
            throw new Exception401("인증되지 않았습니다");
        }
    }

    @MyLog
    public UserResponse.DetailOutDTO 회원상세보기(Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                () -> new Exception400("id", "해당 유저를 찾을 수 없습니다")
        );
        return new UserResponse.DetailOutDTO(userPS);
    }

    public UserResponse.LoginOutDTO 이메일로회원조회(String email) {
        User findUser = userRepository.findByEmail(email).orElseThrow(
                () -> new Exception400("email", "해당 유저를 찾을 수 없습니다.")
        );
        UserResponse.LoginOutDTO loginOutDTO = new UserResponse.LoginOutDTO(findUser.getId(), findUser.getRole());
        return loginOutDTO;
    }


    @Transactional
    public UserResponse.ModifiedOutDTO 개인정보수정(UserRequest.ModifiedInDTO modifiedInDTO, MultipartFile profile, Long id) {
        // 1. 아이디로 회원 조회
        User user = userRepository.findById(id).orElseThrow(() -> new Exception400("id", "해당 유저가 존재하지 않습니다"));

        // 2. 수정사항 없는 경우
        if ((profile == null || profile.isEmpty()) &&
                (modifiedInDTO.getProfileToDelete() == null || modifiedInDTO.getProfileToDelete().isEmpty()) &&
                (user.getEmail().equals(modifiedInDTO.getEmail())) &&
                (user.getUsername().equals(modifiedInDTO.getUsername())) &&
                (modifiedInDTO.getNewPassword() == null || modifiedInDTO.getNewPassword().isEmpty())) {
            throw new Exception400("profile, profileToDelete, email, username, newPassword", "수정사항이 없습니다.");
        }

        boolean isProfileReset = false;
        // 3. 프로필 사진 등록 시
        if (profile != null && !profile.isEmpty()) {
            // 서버에 사진 저장
            try {
                String path = s3Service.upload(profile);
                user.changeProfile(path);
                isProfileReset = true;
            } catch (Exception e) {
                throw new Exception500("프로필사진 변경 실패 : " + e.getMessage());
            }
        }
        // 4. 프로필 사진 삭제 시
        if (modifiedInDTO.getProfileToDelete() != null) {
            try {
                s3Service.delete(modifiedInDTO.getProfileToDelete());
                user.changeProfile("https://lupinbucket.s3.ap-northeast-2.amazonaws.com/person.png");
                isProfileReset = true;
            } catch (Exception e) {
                throw new Exception500("프로필사진 삭제 실패 : " + e.getMessage());
            }
        }
        // 5. 이메일 주소 변경 시
        if (user.getEmail() != modifiedInDTO.getEmail()) {
            user.changeEmail(modifiedInDTO.getEmail());
        }
        // 6. 사원명 변경 시
        if (user.getUsername() != modifiedInDTO.getUsername()) {
            user.changeUsername(modifiedInDTO.getUsername());
        }
        // 7. 비밀번호 변경 시
        boolean isPasswordReset = false;
        if (modifiedInDTO.getNewPassword() != null && !modifiedInDTO.getNewPassword().isEmpty()) {
            String encodePassword = passwordEncoder.encode(modifiedInDTO.getNewPassword());
            user.changePassword(encodePassword);
            isPasswordReset = true;
        }
        // 8. ModifiedOutDTO 생성
        return new UserResponse.ModifiedOutDTO(user, isPasswordReset, isProfileReset);
    }

    @Transactional
    public void 퇴사(Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                () -> new Exception500("로그인 된 유저가 DB에 존재하지 않음")
        );
        userPS.resign();
    }

    @Transactional
    public UserResponse.EmailOutDTO 인증메일보내기(String email) throws Exception {
        MimeMessage message = 인증메시지생성(email);
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new Exception400("email", "인증이메일 전송 실패");
        }

        UserResponse.EmailOutDTO emailOutDTO = new UserResponse.EmailOutDTO(ePassword);
        return emailOutDTO;
    }

    private MimeMessage 인증메시지생성(String email) throws Exception {
        System.out.println("보내는 대상 : " + email);
        System.out.println("인증 번호 : " + ePassword);
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, email);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목

        String msg = "";
        msg += "<div style='margin:20px;'>";
        msg += "<h1> 안녕하세요. </h1>";
        msg += "<br>";
        msg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msg += "<div style='font-size:130%'>";
        msg += "CODE : <strong>";
        msg += ePassword + "</strong><div><br/> ";
        msg += "</div>";
        message.setText(msg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("teamminifive@naver.com", "Lupintech"));//보내는 사람

        return message;
    }

    public static String 인증번호생성() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }
}