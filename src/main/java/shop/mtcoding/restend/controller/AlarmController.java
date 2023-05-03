package shop.mtcoding.restend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.alarm.AlarmResponse;
import shop.mtcoding.restend.service.AlarmService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AlarmController {

    private final AlarmService alarmService;
    @GetMapping("/auth/alarm")
    public ResponseEntity<?> getUserAlarms(@AuthenticationPrincipal MyUserDetails myUserDetails)
    {
        List<AlarmResponse.AlarmOutDTO> alarmOutDTOS = alarmService.findByUserId(myUserDetails.getUser().getId());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(alarmOutDTOS);
        return ResponseEntity.ok(responseDTO);
    }
}
