package kr.co.lupintech.model.leave.enums;

public enum LeaveStatus {
    WAITING,
    APPROVAL,
    REJECTION,
    CANCEL//DB에 저장될 일은 없으나 AlarmDTO 응답할때 사용
}
