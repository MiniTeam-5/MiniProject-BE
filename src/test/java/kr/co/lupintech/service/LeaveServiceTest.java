package kr.co.lupintech.service;

import kr.co.lupintech.dto.leave.LeaveRequest;
import kr.co.lupintech.dto.leave.LeaveResponse;
import kr.co.lupintech.model.alarm.Alarm;
import kr.co.lupintech.model.alarm.AlarmRepository;
import kr.co.lupintech.model.leave.Leave;
import kr.co.lupintech.model.leave.LeaveRepository;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import kr.co.lupintech.core.dummy.DummyEntity;
import kr.co.lupintech.model.leave.enums.LeaveStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static kr.co.lupintech.model.leave.enums.LeaveStatus.*;

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

    @Mock
    private SseService sseService;

    @Test
    public void 연차당직신청하기_test() throws Exception{

        // given
        LeaveRequest.ApplyInDTO applyInDTO = new LeaveRequest.ApplyInDTO();
        applyInDTO.setType(LeaveType.valueOf("DUTY"));
        applyInDTO.setStartDate(LocalDate.parse("2023-07-20"));
        applyInDTO.setEndDate(LocalDate.parse("2023-07-20"));

        // stub 1
        User cos = newMockUser(1L, "박코스", "cos@nate.com", 15);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        // stub 2
        Leave leave = newMockLeave(1L, cos, LeaveType.DUTY,  LocalDate.parse("2023-07-20"), LocalDate.parse("2023-07-20"), 0);
        Mockito.when(leaveRepository.save(any())).thenReturn(leave);

        // stub 3
        Alarm alarm = newMockAlarm(1L, cos, leave);
        Mockito.when(alarmRepository.save(any())).thenReturn(alarm);

        // when
        LeaveResponse.ApplyOutDTO applyOutDTO = leaveService.연차당직신청하기(applyInDTO, 1L);

        // then
        Assertions.assertThat(applyOutDTO.getId()).isEqualTo(1L);
        Assertions.assertThat(applyOutDTO.getType()).isEqualTo(LeaveType.DUTY);
        Assertions.assertThat(applyOutDTO.getUsingDays()).isEqualTo(0);
        Assertions.assertThat(applyOutDTO.getRemainDays()).isEqualTo(15);
        Assertions.assertThat(applyOutDTO.getStatus()).isEqualTo(WAITING);
    }

    @Test
    public void 연차신청취소하기_test() throws Exception{
        // given
        Long id = 1L;

        // stub 1
        User cos = newMockUser(1L, "박코스", "cos@nate.com",8);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        // stub 2
        Leave leave = newMockLeave(1L, cos, LeaveType.ANNUAL,  LocalDate.parse("2023-07-20"), LocalDate.parse("2023-07-20"), 1);
        Mockito.when(leaveRepository.findById(any())).thenReturn(Optional.of(leave));

        // stub 3
        Alarm alarm = newMockAlarm(1L, cos, leave);
        Mockito.when(alarmRepository.save(any())).thenReturn(alarm);

        // when
        LeaveResponse.CancelOutDTO cancelOutDTO = leaveService.연차당직신청취소하기(1L, 1L);

        // then
        Assertions.assertThat(cancelOutDTO.getRemainDays()).isEqualTo(9);
    }

    @Test
    public void 당직신청취소하기_test() throws Exception{
        // given
        Long id = 1L;

        // stub 1
        User cos = newMockUser(1L, "박코스", "cos@nate.com",8);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        // stub 2
        Leave leave = newMockLeave(1L, cos, LeaveType.DUTY,  LocalDate.parse("2023-07-20"), LocalDate.parse("2023-07-20"), 1);
        Mockito.when(leaveRepository.findById(any())).thenReturn(Optional.of(leave));

        // stub 3
        Alarm alarm = newMockAlarm(1L, cos, leave);
        Mockito.when(alarmRepository.save(any())).thenReturn(alarm);

        // when
        LeaveResponse.CancelOutDTO cancelOutDTO = leaveService.연차당직신청취소하기(1L, 1L);

        // then
        Assertions.assertThat(cancelOutDTO.getRemainDays()).isEqualTo(8);
    }

    @Test
    public void 연차당직결정하기_test() throws Exception {

        // given
        LeaveRequest.DecideInDTO decideInDTO = new LeaveRequest.DecideInDTO();
        decideInDTO.setId(1L);
        decideInDTO.setStatus(APPROVAL);

        // stub 1
        User cos = newMockUser(1L, "박코스", "cos@nate.com",14);
        Leave leave = newMockLeave(1L, cos, LeaveType.ANNUAL, LocalDate.parse("2023-07-20"), LocalDate.parse("2023-07-20"), 0);
        Mockito.when(leaveRepository.findById(any())).thenReturn(Optional.ofNullable(leave));

        // stub 2
        Alarm alarm = newMockAlarm(1L, cos, leave);
        Mockito.when(alarmRepository.save(any())).thenReturn(alarm);

        // when
        LeaveResponse.DecideOutDTO decideOutDTO = leaveService.연차당직결정하기(decideInDTO);

        // then
        Assertions.assertThat(decideOutDTO.getRemainDays()).isEqualTo(14);
    }

    @Test
    public void 모든유저특정월기준정보_Test() {

        User user1 = User.builder().id(1L).username("도토리").build();
        User user2 = User.builder().id(2L).username("토마토").build();
        User user3 = User.builder().id(3L).username("김딸기").build();

        Leave leave1 = Leave.builder()
                .user(user1)
                .type(LeaveType.ANNUAL)
                .startDate(LocalDate.of(2023, 2, 25))
                .endDate(LocalDate.of(2023, 3, 5))
                .status(WAITING)
                .build();

        Leave leave2 = Leave.builder()
                .user(user2)
                .type(LeaveType.DUTY)
                .startDate(LocalDate.of(2023, 4, 3))
                .endDate(LocalDate.of(2023, 5, 4))
                .status(APPROVAL)
                .build();

        Leave leave3 = Leave.builder()
                .user(user3)
                .type(LeaveType.ANNUAL)
                .startDate(LocalDate.of(2023, 5, 27))
                .endDate(LocalDate.of(2023, 6, 5))
                .status(REJECTION)
                .build();

        List<Leave> leaveList = Arrays.asList(leave1, leave2, leave3);

        //Mockito.when(leaveRepository.findAllByUserId(any())).thenReturn(leaveList);
        Mockito.when(leaveRepository.findAll()).thenReturn(leaveList);

        // when
        List<LeaveResponse.InfoOutDTO> result = leaveService.연차당직정보가져오기세달치("2023-04");

        // then
        assertEquals(3, result.size());
        org.junit.jupiter.api.Assertions.assertEquals(leave1.getUser().getId(), result.get(0).getUserId());
        org.junit.jupiter.api.Assertions.assertEquals(leave1.getUser().getUsername(), result.get(0).getUsername());
        org.junit.jupiter.api.Assertions.assertEquals(leave1.getType(), result.get(0).getType());
        assertEquals(leave1.getStatus(), result.get(0).getStatus());
        assertEquals(leave1.getStartDate().toString(), result.get(0).getStartDate().toString());
        assertEquals(leave1.getEndDate().toString(), result.get(0).getEndDate().toString());
        org.junit.jupiter.api.Assertions.assertEquals(leave2.getUser().getId(), result.get(1).getUserId());
        org.junit.jupiter.api.Assertions.assertEquals(leave2.getUser().getUsername(), result.get(1).getUsername());
        org.junit.jupiter.api.Assertions.assertEquals(leave2.getType(), result.get(1).getType());
        assertEquals(leave2.getStatus(), result.get(1).getStatus());
        assertEquals(leave2.getStartDate().toString(), result.get(1).getStartDate().toString());
        assertEquals(leave2.getEndDate().toString(), result.get(1).getEndDate().toString());
    }

    @Test
    public void 상태선택연차당직정보가져오기_Test() {

        // given
        User user1 = User.builder().id(1L).username("도토리").build();
        User user2 = User.builder().id(2L).username("토마토").build();

        Leave leave1 = Leave.builder()
                .user(user1)
                .type(LeaveType.ANNUAL)
                .startDate(LocalDate.of(2023, 3, 25))
                .endDate(LocalDate.of(2023, 3, 28))
                .status(WAITING)
                .build();

        Leave leave2 = Leave.builder()
                .user(user2)
                .type(LeaveType.DUTY)
                .startDate(LocalDate.of(2023, 4, 1))
                .endDate(LocalDate.of(2023, 4, 1))
                .status(WAITING)
                .build();

        List<Leave> leaveList = Arrays.asList(leave1, leave2);

        Mockito.when(leaveRepository.findByStatus(LeaveStatus.WAITING)).thenReturn(leaveList);

        // when
        List<LeaveResponse.InfoOutDTO> result = leaveService.상태선택연차당직정보가져오기(LeaveStatus.WAITING);

        // then
        assertEquals(2, result.size());
        org.junit.jupiter.api.Assertions.assertEquals(leave1.getUser().getId(), result.get(0).getUserId());
        org.junit.jupiter.api.Assertions.assertEquals(leave1.getUser().getUsername(), result.get(0).getUsername());
        org.junit.jupiter.api.Assertions.assertEquals(leave1.getType(), result.get(0).getType());
        assertEquals(leave1.getStatus(), result.get(0).getStatus());
        assertEquals(leave1.getStartDate().toString(), result.get(0).getStartDate().toString());
        assertEquals(leave1.getEndDate().toString(), result.get(0).getEndDate().toString());
        org.junit.jupiter.api.Assertions.assertEquals(leave2.getUser().getId(), result.get(1).getUserId());
        org.junit.jupiter.api.Assertions.assertEquals(leave2.getUser().getUsername(), result.get(1).getUsername());
        org.junit.jupiter.api.Assertions.assertEquals(leave2.getType(), result.get(1).getType());
        assertEquals(leave2.getStatus(), result.get(1).getStatus());
        assertEquals(leave2.getStartDate().toString(), result.get(1).getStartDate().toString());
        assertEquals(leave2.getEndDate().toString(), result.get(1).getEndDate().toString());
    }
}
