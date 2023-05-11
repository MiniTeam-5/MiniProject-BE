package kr.co.lupintech.service;

import kr.co.lupintech.core.exception.Exception500;
import kr.co.lupintech.dto.alarm.AlarmResponse;
import kr.co.lupintech.model.alarm.Alarm;
import kr.co.lupintech.model.alarm.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public List<AlarmResponse.AlarmOutDTO> findByUserId(Long userId) {
        List<Alarm> alarms = alarmRepository.findByUserId(userId);
        return alarms.stream()
                .map(AlarmResponse.AlarmOutDTO::new)
                .collect(Collectors.toList());
    }

    public Alarm save(Alarm alarm) {
        try {
            return alarmRepository.save(alarm);
        }catch (Exception e){
            throw new Exception500("알람 저장 실패 : "+e.getMessage());
        }
    }
}
