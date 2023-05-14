package kr.co.lupintech.core.factory;

import kr.co.lupintech.model.alarm.Alarm;
import kr.co.lupintech.model.leave.Leave;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;

import java.time.LocalDate;

public class AlarmFactory {

    public static Alarm newAlarm(User user, Leave leave){
        Alarm alarm = Alarm.builder().user(user).leave(leave).build();
        leave.getAlarms().add(alarm);

        return alarm;
    }

}
