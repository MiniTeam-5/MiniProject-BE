package kr.co.lupintech.dto.alarm;

import kr.co.lupintech.model.alarm.Alarm;
import kr.co.lupintech.model.leave.Leave;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AlarmResponse {

    @Setter
    @Getter
    public static class AlarmOutDTO {
        private Long id;

        private String username;
        private LeaveType type;

        private LocalDate startDate;
        private LocalDate endDate;
        private Integer usingDays;

        private LeaveStatus status;

        private LocalDateTime createdAt;

        public AlarmOutDTO(Alarm alarm) {
            this.id = alarm.getId();
            this.username = alarm.getUser().getUsername();
            this.type = alarm.getLeave().getType();
            this.startDate = alarm.getLeave().getStartDate();
            this.endDate = alarm.getLeave().getEndDate();
            this.usingDays = alarm.getLeave().getUsingDays();
            this.status = alarm.getLeave().getStatus();
            this.createdAt = alarm.getCreatedAt();
        }
        public AlarmOutDTO(){}
    }

}
