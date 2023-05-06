package shop.mtcoding.restend.dto.alarm;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.alarm.Alarm;
import shop.mtcoding.restend.model.user.User;

import java.time.LocalDateTime;

public class AlarmResponse {

    @Setter
    @Getter

    public static class AlarmOutDTO {
        private Long id;
        private String content;
        private LocalDateTime createdAt;

        public AlarmOutDTO(Alarm alarm) {
            this.id = alarm.getId();
            this.content = alarm.getContent();
            this.createdAt = alarm.getCreatedAt();
        }
        public AlarmOutDTO(){}
    }

}
