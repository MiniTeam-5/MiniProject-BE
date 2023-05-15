package kr.co.lupintech.service;

import kr.co.lupintech.core.exception.Exception400;
import kr.co.lupintech.dto.alarm.AlarmResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(Long userId) {

        SseEmitter emitter = new SseEmitter(20 * 60 * 1000L);//20분

        emitters.put(userId, emitter);
        emitter.onCompletion(() -> {
            emitters.remove(userId);
        });
        emitter.onTimeout(() -> {
            emitter.complete();
        });

        return emitter;
    }

    public void sendToUser(Long userId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                throw new Exception400("id", "연결되지 않은 유저입니다.");
            }
        }
    }

    public void sendMessage(Long userId)
    {
        AlarmResponse.AlarmOutDTO alarmOutDTO = new AlarmResponse.AlarmOutDTO();
        alarmOutDTO.setId(99999999L);
        alarmOutDTO.setCreatedAt(LocalDateTime.now());
        sendToUser(userId, "alarm", alarmOutDTO);
    }
    public boolean remove(Long userId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            emitter.complete();
            emitters.remove(userId);
            return true;
        }
        return false;
    }

    public void removeAll() {
        emitters.values().forEach(emitter -> {
            emitter.complete();
        });
        emitters.clear();
        log.info("All SseEmitters removed");
    }
}
