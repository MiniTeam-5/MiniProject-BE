package shop.mtcoding.restend.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.dto.leave.LeaveRequest;
import shop.mtcoding.restend.dto.leave.LeaveResponse;
import shop.mtcoding.restend.model.alarm.Alarm;
import shop.mtcoding.restend.model.alarm.AlarmRepository;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.LeaveRepository;
import shop.mtcoding.restend.model.leave.enumUtil.LeaveStatus;
import shop.mtcoding.restend.model.leave.enumUtil.LeaveType;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class LeaveServiceTest extends DummyEntity {
    @InjectMocks
    private LeaveService leaveService;

    // 가짜 객체를 만들어서 Mockito 환경에 Load
    @Mock
    private UserRepository userRepository;
    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private AlarmRepository alarmRepository;

    @Test
    public void 연차당직신청하기_test() throws Exception{

        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("DUTY"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-20"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-20"));

        // stub 1
        User cos = newMockUser(1L, "cos", "코스");
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        // stub 2
        Alarm alarm = newMockAlarm(1L, cos, "당직이 등록되었습니다.");
        Mockito.when(alarmRepository.save(any())).thenReturn(alarm);

        // stub 3
        Leave leave = newMockLeave(1L, cos, LeaveType.DUTY,  LocalDate.parse("2023-07-20"), LocalDate.parse("2023-07-20"));
        Mockito.when(leaveRepository.save(any())).thenReturn(leave);

        // when
        LeaveResponse.ApplyOutDTO applyOutDTO = leaveService.연차당직신청하기(applyInDTO, 1L);

        // then
        Assertions.assertThat(applyOutDTO.getId()).isEqualTo(1L);
        Assertions.assertThat(applyOutDTO.getStatus()).isEqualTo(LeaveStatus.WAITING);
    }
}
