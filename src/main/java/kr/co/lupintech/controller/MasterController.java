package kr.co.lupintech.controller;

import kr.co.lupintech.dto.ResponseDTO;
import kr.co.lupintech.dto.user.UserRequest;
import kr.co.lupintech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class MasterController {
    private final UserService userService;

    // 권한 변경
    @PostMapping("/master/{id}")
    public ResponseEntity<?> roleUpdate(@PathVariable Long id, @RequestBody @Valid UserRequest.MasterInDTO masterInDTO){
        userService.권한수정(id, masterInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }
}
