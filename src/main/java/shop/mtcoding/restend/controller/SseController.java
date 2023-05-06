package shop.mtcoding.restend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.dto.alarm.AlarmResponse;
import shop.mtcoding.restend.service.SseService;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/auth/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect( @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Long userId = myUserDetails.getUser().getId();
        SseEmitter emitter = sseService.add(userId);

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("You are connected!"));
        }
        catch (IOException e)
        {
            sseService.remove(userId);
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(emitter);
    }

    @PostMapping("/auth/disconnect")
    public ResponseEntity<?> disconnect(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        Long userId = myUserDetails.getUser().getId();
        boolean disconnected = sseService.remove(userId);

        if (disconnected) {
            return ResponseEntity.ok().build();
        } else {
            throw new Exception400("id", "연결되지 않은 유저입니다.");
        }
    }

    @GetMapping("/auth/msg")
    public ResponseEntity<?> message(@AuthenticationPrincipal MyUserDetails myUserDetails )
    {
        Long userId = myUserDetails.getUser().getId();
        sseService.sendMessage(userId);

        return ResponseEntity.ok().build();
    }

}