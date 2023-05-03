package shop.mtcoding.restend.dto.leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.enumUtil.LeaveStatus;
import shop.mtcoding.restend.model.leave.enumUtil.LeaveType;
import shop.mtcoding.restend.model.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
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


        public Leave toEntity(User user){
            return Leave.builder()
                    .type(type)
                    .user(user)
                    .startDate(startDate)
                    .endDate(endDate)
                    .status(LeaveStatus.WAITING)
                    .build();

        }
    }
}
