package kr.co.lupintech.controller;

import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import kr.co.lupintech.core.auth.session.MyUserDetails;
import kr.co.lupintech.core.exception.Exception400;
import kr.co.lupintech.core.exception.Exception500;
import kr.co.lupintech.service.SseService;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SseController {

    private final SseService sseService;

    private final UserRepository userRepository;

    @GetMapping(value = "/auth/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect( @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Long userId = myUserDetails.getUser().getId();
        SseEmitter emitter = sseService.add(userId);

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("You are connected!"));

            Optional<User> userPS = userRepository.findById(myUserDetails.getUser().getId());
            log.info("{} connected", userPS.get().getUsername());
        }
        catch (IOException e)
        {
            sseService.remove(userId);
            throw new Exception500("sse 전송실패");
        }

        return ResponseEntity.ok(emitter);
    }

    @PostMapping("/auth/disconnect")
    public ResponseEntity<?> disconnect(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        Long userId = myUserDetails.getUser().getId();
        boolean disconnected = sseService.remove(userId);

        if (disconnected) {
            Optional<User> userPS = userRepository.findById(myUserDetails.getUser().getId());
            log.info("{} disconnected", userPS.get().getUsername());
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