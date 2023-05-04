package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.mtcoding.restend.core.annotation.MyErrorLog;
import shop.mtcoding.restend.core.annotation.MyLog;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception403;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.service.UserService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @MyErrorLog
    @MyLog
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinInDTO joinInDTO, Errors errors) {
        UserResponse.JoinOutDTO joinOutDTO = userService.회원가입(joinInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors){
        String jwt = userService.로그인(loginInDTO);
        UserResponse.LoginOutDTO loginOutDTO = userService.이메일로회원조회(loginInDTO.getEmail());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO);
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(responseDTO);
    }

    @GetMapping("/auth/user/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        if(id.longValue() != myUserDetails.getUser().getId()){
            throw new Exception403("권한이 없습니다");
        }
        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(id);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(detailOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/auth/user/{id}")
    public ResponseEntity<?> modifyProfile(@PathVariable Long id,
                                           @RequestParam(required = false) MultipartFile profile,
                                           @ModelAttribute @Valid UserRequest.ModifyInDTO modifyInDTO, BindingResult bindingResult,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {
        if(id.longValue() != myUserDetails.getUser().getId()){
            throw new Exception403("권한이 없습니다");
        }

        if (profile != null){
            if (!profile.isEmpty()) {
                userService.프로필사진변경(modifyInDTO, profile);
            }
        }

        UserResponse.ModifiedOutDTO modifiedOutDTO = userService.개인정보수정(modifyInDTO, id);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(modifiedOutDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
