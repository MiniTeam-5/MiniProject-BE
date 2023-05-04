package shop.mtcoding.restend.dto.leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.leave.enums.LeaveType;
import shop.mtcoding.restend.model.user.User;

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
}
