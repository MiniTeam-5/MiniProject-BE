package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import shop.mtcoding.restend.core.annotation.MyLog;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.core.exception.Exception401;
import shop.mtcoding.restend.core.exception.Exception500;
import shop.mtcoding.restend.core.util.MyFileUtil;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Value("${file.path}")
    private String uploadFolder;

    @MyLog
    @Transactional
    public UserResponse.JoinOutDTO 회원가입(UserRequest.JoinInDTO joinInDTO){
        Optional<User> userByName =userRepository.findByUsername(joinInDTO.getUsername());
        if(userByName.isPresent()){
            // 이 부분이 try catch 안에 있으면 Exception500에게 제어권을 뺏긴다.
            throw new Exception400("username", "유저네임이 존재합니다");
        }

        Optional<User> userByEmail = userRepository.findByEmail(joinInDTO.getEmail());
        if (userByEmail.isPresent()) {
            throw new Exception400("email", "해당 이메일 주소는 사용중입니다");
        }

        String encPassword = passwordEncoder.encode(joinInDTO.getPassword()); // 60Byte
        joinInDTO.setPassword(encPassword);
//        System.out.println("encPassword : "+encPassword);

        // 디비 save 되는 쪽만 try catch로 처리하자.
        try {
            User userPS = userRepository.save(joinInDTO.toEntity());
            return new UserResponse.JoinOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500("회원가입 실패 : "+e.getMessage());
        }
    }

    @MyLog
    public String 로그인(UserRequest.LoginInDTO loginInDTO) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            return MyJwtProvider.create(myUserDetails.getUser());
        }catch (Exception e){
            throw new Exception401("인증되지 않았습니다");
        }
    }

    @MyLog
    public UserResponse.DetailOutDTO 회원상세보기(Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")

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


    public User 회원조회(Long id) {
        User findUser = userRepository.findById(id).orElseThrow(
                () -> new Exception400("id", "해당 유저가 존재하지 않습니다")
        );
        return findUser;
    }

    @Transactional
    public void 이메일변경(User userPS, String email) {
        userPS.changeEmail(email);
    }



    @Transactional
    public UserResponse.ModifiedOutDTO 개인정보수정(UserRequest.ModifyInDTO modifyInDTO, Long id) {

        User user = userRepository.findById(id).orElseThrow(()->new Exception400("id","해당 유저가 존재하지 않습니다"));
        UserResponse.ModifiedOutDTO modifiedOutDTO = new UserResponse.ModifiedOutDTO(user);

        if (user.getEmail() != modifyInDTO.getEmail()) {
            user.changeEmail(modifyInDTO.getEmail());
        }
        if (user.getUsername() != modifyInDTO.getUsername()) {
            user.changeUsername(modifyInDTO.getUsername());
        }
        if (!modifyInDTO.getNewPassword().isEmpty()) {
            if (modifyInDTO.getNewPassword().equals(modifyInDTO.getCheckPassword())) {
                String encodePassword = passwordEncoder.encode(modifyInDTO.getNewPassword());
                user.changePassword(encodePassword);
                modifiedOutDTO.setPasswordReset(true);
            } else {
                throw new Exception400("password", "비밀번호 재확인 필요");
            }
        }

        return modifiedOutDTO;
    }

    @Transactional
    public void 프로필사진변경(UserRequest.ModifyInDTO modifyInDTO, MultipartFile profile) {
        String uuidImageName = MyFileUtil.write(uploadFolder, profile);
        try {
            modifyInDTO.setProfile(uploadFolder+uuidImageName);
        } catch (Exception e) {
            throw new Exception500("프로필사진 변경 실패 : " + e.getMessage());
        }
    }
}
