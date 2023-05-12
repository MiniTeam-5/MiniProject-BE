package kr.co.lupintech.core.factory;

import kr.co.lupintech.model.alarm.Alarm;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;

import java.time.LocalDate;

public class AlarmFactory {

    public static Alarm newAlarm(User user, LocalDate startDate, LocalDate endDate, int usingDays, LeaveType type, LeaveStatus status){
        return Alarm.builder()
                .user(user)
                .content(user.getUsername()+","+startDate.toString()+","+endDate.toString()+","+usingDays+","+type+","+status)
                .build();
    }

}
