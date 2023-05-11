package kr.co.lupintech.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.lupintech.core.annotation.MyErrorLog;
import kr.co.lupintech.core.annotation.MyLog;
import kr.co.lupintech.core.auth.jwt.MyJwtProvider;
import kr.co.lupintech.core.auth.session.MyUserDetails;
import kr.co.lupintech.dto.user.UserRequest;
import kr.co.lupintech.dto.user.UserResponse;
import kr.co.lupintech.service.RefreshService;
import kr.co.lupintech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import kr.co.lupintech.dto.ResponseDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    private final RefreshService refreshService;

    @MyErrorLog
    @MyLog
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinInDTO joinInDTO, Errors errors) {
        UserResponse.JoinOutDTO joinOutDTO = userService.회원가입(joinInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors) {
        Pair<String, String > tokenInfo = userService.로그인(loginInDTO);
        UserResponse.LoginOutDTO loginOutDTO = userService.이메일로회원조회(loginInDTO.getEmail());

        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO);
        return ResponseEntity.ok()
                .header(MyJwtProvider.HEADER, tokenInfo.getFirst())
                .header(MyJwtProvider.HEADER_REFRESH, tokenInfo.getSecond())
                .body(responseDTO);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){

        refreshService.리프레시토큰회수(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/user")
    public ResponseEntity<?> detail(@AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(myUserDetails.getUser().getId());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(detailOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }


    @PostMapping("/auth/user")
    public ResponseEntity<?> modifyProfile(@RequestPart(value = "profile", required = false) MultipartFile profile,
                                           @RequestPart(value = "modifiedInDTO") @Valid UserRequest.ModifiedInDTO modifiedInDTO, Errors errors,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {

        UserResponse.ModifiedOutDTO modifiedOutDTO = userService.개인정보수정(modifiedInDTO, profile, myUserDetails.getUser().getId());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(modifiedOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/admin/resign/{id}")
    public ResponseEntity<?> resign(@PathVariable Long id){
        userService.퇴사(id);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();

        return ResponseEntity.ok(responseDTO);
    }
}

