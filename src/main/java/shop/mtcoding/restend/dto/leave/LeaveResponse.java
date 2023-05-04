package shop.mtcoding.restend.dto.leave;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.leave.enums.LeaveType;
import shop.mtcoding.restend.model.user.User;

public class LeaveResponse {

    @Setter @Getter
    public static class ApplyOutDTO {
        private Long id;
        private LeaveType type;
        private Integer usingDays; // 신청한 연차수 (당직의 경우엔 0)
        private Integer remainDays; // 본인의 남은 연차수
        private LeaveStatus status;

        public ApplyOutDTO(Leave leave, User user) {
            this.id = leave.getId();
            this.type = leave.getType();
            this.usingDays = leave.getUsingDays();
            this.remainDays = user.getRemainDays();
            this.status =leave.getStatus();
        }
    }

    @Setter @Getter
    public static class CancelOutDTO {
        private Integer remainDays; // 본인의 남은 연차수

        public CancelOutDTO(User user) {
            this.remainDays = user.getRemainDays();
        }
    }
}
