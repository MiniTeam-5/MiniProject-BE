package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import shop.mtcoding.restend.dto.manage.Manage;
import shop.mtcoding.restend.core.util.MyFileUtil;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

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
        //3. 비밀번호, 비밀번호 확인 일치하는지 확인
        if (!joinInDTO.getPassword().equals(joinInDTO.getCheckPassword())) {
            throw new Exception400("password", "패스워드가 일치하지 않습니다");
        }
        //4. 비밀번호 암호화
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
    public String 로그인(UserRequest.LoginInDTO loginInDTO) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            return MyJwtProvider.create(myUserDetails.getUser());
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

    // 회원 관리 페이지, 회원 정보 수정 ( 5/2 김형준 추가)
    @MyLog
    @Transactional
    public Manage 연차수정(Long id, Manage.AnnualRequestDTO annualRequestDTO) {
        Manage manage = new Manage();
        User userPS = userRepository.findById(id)
                .orElseThrow(()->new Exception400("id", "해당 유저가 존재하지 않습니다"));
        // 정보 수정
        Manage.AnnualRequestDTO managePS = new Manage.AnnualRequestDTO(annualRequestDTO.getRemain_days());
        userPS.update(managePS.toEntityIn());
        return manage.toEntityOut(userPS);
    } // 더티체킹

    // role까지 변경가능
    @MyLog
    @Transactional
    public Manage.MasterOutDTO 권한수정(Long id, Manage.MasterInDTO masterIn) {
        Manage.MasterOutDTO managePS = new Manage.MasterOutDTO();
        User userPS = userRepository.findById(id)
                .orElseThrow(()->new Exception400("id", "해당 유저가 존재하지 않습니다"));
        // 정보 수정
        userPS.update(masterIn.toEntityIn(id));
        return managePS.toEntityOut(userPS);
    } // 더티체킹

    // checkpoint : 유저 목록을 Page객체로 전달할것인가, List객체로 전달할 것인가.
    @MyLog
    @Transactional(readOnly = true)
    public Page<Manage.UserManageDTO> 회원목록보기(Pageable pageable){

        Manage.UserManageDTO userManageDTO = new Manage.UserManageDTO();
        List<User> userList = userRepository.findAll();
        Page<Manage.UserManageDTO> usersPG = new PageImpl<>(userList.stream().map(user -> userManageDTO.toEntityOut(user) ).collect(Collectors.toList()), pageable, userList.size());
        return usersPG;
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

        //1. 아이디로 회원 조회
        User user = userRepository.findById(id).orElseThrow(() -> new Exception400("id", "해당 유저가 존재하지 않습니다"));
        //2. ModifiedOutDTO 생성
        UserResponse.ModifiedOutDTO modifiedOutDTO = new UserResponse.ModifiedOutDTO(user);

        //3. 수정사항 없는 경우
        if (profile.isEmpty() && modifiedInDTO.getDeletedProfile() == null &&
                modifiedInDTO.getNewPassword() == null &&
                user.getEmail().equals(modifiedInDTO.getEmail()) &&
                user.getUsername().equals(modifiedInDTO.getUsername())) {
            throw new Exception400("id", "수정사항이 없습니다.");
        }

        //4. 프로필 사진 등록 시
        if (profile != null && !profile.isEmpty()) {
            //서버에 사진 저장
            String uuidImageName = MyFileUtil.write(uploadFolder, profile);
            try {
                user.changeProfile(uploadFolder + uuidImageName);
                modifiedOutDTO.setProfileReset(true);
            } catch (Exception e) {
                throw new Exception500("프로필사진 변경 실패 : " + e.getMessage());
            }
        }
        //5. 프로필 사진 삭제 시
        if (modifiedInDTO.getDeletedProfile() != null && modifiedInDTO.getDeletedProfile().equals(true)) {
            user.changeProfile(uploadFolder + "person.png");
            modifiedOutDTO.setProfileReset(true);
        }
        //6. 이메일 주소 변경 시
        if (user.getEmail() != modifiedInDTO.getEmail()) {
            user.changeEmail(modifiedInDTO.getEmail());
            modifiedOutDTO.setEmail(modifiedInDTO.getEmail());
        }
        //7. 사원명 변경 시
        if (user.getUsername() != modifiedInDTO.getUsername()) {
            user.changeUsername(modifiedInDTO.getUsername());
            modifiedOutDTO.setUsername(modifiedInDTO.getUsername());
        }
        //8. 비밀번호 변경 시
        if (modifiedInDTO.getNewPassword() != null && !modifiedInDTO.getNewPassword().isEmpty()) {
            if (modifiedInDTO.getNewPassword().equals(modifiedInDTO.getCheckPassword())) {
                String encodePassword = passwordEncoder.encode(modifiedInDTO.getNewPassword());
                user.changePassword(encodePassword);
                modifiedOutDTO.setPasswordReset(true);
            } else {
                throw new Exception400("password", "비밀번호 재확인 필요");
            }
        }
        return modifiedOutDTO;
    }

    @Transactional
    public void 퇴사(Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                () -> new Exception500("로그인 된 유저가 DB에 존재하지 않음")
        );
        userPS.resign();
    }
}
