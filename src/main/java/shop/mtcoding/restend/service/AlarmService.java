package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.dto.alarm.AlarmResponse;
import shop.mtcoding.restend.model.alarm.Alarm;
import shop.mtcoding.restend.model.alarm.AlarmRepository;

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

}
