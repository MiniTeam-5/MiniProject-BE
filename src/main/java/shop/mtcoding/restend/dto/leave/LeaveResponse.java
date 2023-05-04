package shop.mtcoding.restend.dto.leave;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.leave.enums.LeaveType;
import shop.mtcoding.restend.model.user.User;

public class LeaveResponse {
    /*
    {code: “201”, status: “success”, data: {id: 1, status: “waiting”}}
     */
    @Setter
    @Getter
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

    @Setter
    @Getter
    public static class InfoOutDTO {

        private Long userId;
        private String username;
        private LeaveType type;
        private LeaveStatus status;
        private String startDate;
        private String endDate;

        public InfoOutDTO(Long userId, String username, LeaveType type, LeaveStatus status, String startDate, String endDate) {
            this.userId = userId;
            this.username = username;
            this.type = type;
            this.status = status;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        // Leave 엔티티와 User 엔티티를 사용하여 DTO를 생성하는 생성자
        public InfoOutDTO(Leave leave, User user) {
            this.userId = user.getId();
            this.username = user.getUsername();
            this.type = leave.getType();
            this.status = leave.getStatus();
            this.startDate = leave.getStartDate().toString();
            this.endDate = leave.getEndDate().toString();
        }
    }
}
