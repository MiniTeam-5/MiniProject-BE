package kr.co.lupintech.controller;

import kr.co.lupintech.core.auth.session.MyUserDetails;
import kr.co.lupintech.dto.alarm.AlarmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import kr.co.lupintech.dto.ResponseDTO;
import kr.co.lupintech.service.AlarmService;

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
