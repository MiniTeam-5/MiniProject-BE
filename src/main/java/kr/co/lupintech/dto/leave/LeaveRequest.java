package kr.co.lupintech.dto.leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.lupintech.model.leave.Leave;
import lombok.Getter;
import lombok.Setter;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

public class LeaveRequest {

    @Getter @Setter
    public static class ApplyInDTO {
        private LeaveType type;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent
        private LocalDate startDate;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent
        private LocalDate endDate;


        public Leave toEntity(User user, Integer usingDays){
            return Leave.builder()
                    .type(type)
                    .user(user)
                    .startDate(startDate)
                    .endDate(endDate)
                    .usingDays(usingDays)
                    .status(LeaveStatus.WAITING)
                    .build();

        }
    }

    @Getter @Setter
    public static class DecideInDTO {
        private long id;
        private LeaveStatus status;
    }
}
