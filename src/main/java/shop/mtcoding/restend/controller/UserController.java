package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.mtcoding.restend.core.annotation.MyErrorLog;
import shop.mtcoding.restend.core.annotation.MyLog;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception403;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.token.TokenResponse;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.service.RefreshService;
import shop.mtcoding.restend.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static shop.mtcoding.restend.core.auth.jwt.MyJwtProvider.HEADER;
import static shop.mtcoding.restend.core.auth.jwt.MyJwtProvider.HEADER_REFRESH;

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
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginInDTO loginInDTO, Errors errors) {
        TokenResponse tokenResponse = userService.로그인(loginInDTO);
        UserResponse.LoginOutDTO loginOutDTO = userService.이메일로회원조회(loginInDTO.getEmail());

        ResponseDTO<?> responseDTO = new ResponseDTO<>(loginOutDTO);
        return ResponseEntity.ok()
                .header(HEADER, tokenResponse.getAccessToken())
                .header(HEADER_REFRESH, tokenResponse.getRefreshToken())
                .body(responseDTO);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){

        refreshService.리프레시토큰회수(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/user/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
        if (id.longValue() != myUserDetails.getUser().getId()) {
            throw new Exception403("권한이 없습니다");
        }
        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(id);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(detailOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/auth/user/{id}")
    public ResponseEntity<?> modifyProfile(@PathVariable Long id,
                                           @RequestPart(value = "profile", required = false) MultipartFile profile,
                                           @RequestPart(value = "modifiedInDTO") @Valid UserRequest.ModifiedInDTO modifiedInDTO, Errors errors,
                                           @AuthenticationPrincipal MyUserDetails myUserDetails) {

        if (id.longValue() != myUserDetails.getUser().getId()) {
            throw new Exception403("권한이 없습니다");
        }

        UserResponse.ModifiedOutDTO modifiedOutDTO = userService.개인정보수정(modifiedInDTO, profile, id);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(modifiedOutDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
