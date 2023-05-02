package shop.mtcoding.restend.dto.leave;

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

    /*
    {
”type” : “annual”,
”start_date” : “2023-04-27”,
”end_date” : “2023-04-28”
}
     */
    @Getter @Setter
    public static class ApplyInDTO {
        @NotEmpty
        private LeaveType type;

        @NotEmpty
        @FutureOrPresent
        private LocalDate startDate;

        @NotEmpty
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
