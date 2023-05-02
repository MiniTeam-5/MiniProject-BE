package shop.mtcoding.restend.dto.leave;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.enumUtil.LeaveStatus;
import shop.mtcoding.restend.model.user.User;

public class LeaveResponse {
    /*
    {code: “201”, status: “success”, data: {id: 1, status: “waiting”}}
     */
    @Setter
    @Getter
    public static class ApplyOutDTO {
        private Long id;
        private LeaveStatus status;

        public ApplyOutDTO(Leave leave) {
            this.id = leave.getId();
            this.status =leave.getStatus();
        }
    }
}
