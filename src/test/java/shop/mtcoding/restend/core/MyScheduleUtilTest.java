package shop.mtcoding.restend.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.mtcoding.restend.core.util.MyScheduleUtil;
import shop.mtcoding.restend.model.leave.Leave;
import shop.mtcoding.restend.model.leave.LeaveRepository;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.service.DateService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyScheduleUtilTest {

    @InjectMocks
    private MyScheduleUtil myScheduleUtil;

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DateService dateService;

    private Leave leave;
    private User user;
    private User newcomer;
    private List<Leave> leaveList;
    private List<User> userList;
    private List<User> newcomerList;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .hireDate(LocalDate.now().minusYears(1)) // 1년차 => 15일 이어야 함.
                .remainDays(0)
                .build();
        newcomer = User.builder()
                .hireDate(LocalDate.now().minusMonths(2)) // 신입 (2달차) => 1일 이어야 함.
                .remainDays(1)
                .build();

        leave = Leave.builder()
                .user(user)
                .startDate(LocalDate.now())
                .status(LeaveStatus.WAITING)
                .usingDays(1)
                .build();

        leaveList = Arrays.asList(leave);
        userList = Arrays.asList(user);
        newcomerList = Arrays.asList(newcomer);
    }

    @Test
    public void everydayWaitingRemoveTest() {
        when(leaveRepository.findByStartDateAndStatus(any(LocalDate.class), any(LeaveStatus.class)))
                .thenReturn(leaveList);

        myScheduleUtil.everydayWaitingRemove();

        verify(leaveRepository, times(1)).findByStartDateAndStatus(any(LocalDate.class), any(LeaveStatus.class));
        verify(leaveRepository, times(1)).delete(any(Leave.class));
    }

    @Test
    public void everydayUpdateAnnualDaysTest() {
        when(userRepository.findByHireDate(any(LocalDate.class), anyInt(), anyInt())).thenReturn(userList);
        when(dateService.getAnnualLimit(any(LocalDate.class))).thenReturn(15);

        myScheduleUtil.everydayUpdateAnnualDays();

        verify(userRepository, times(1)).findByHireDate(any(LocalDate.class), anyInt(), anyInt());
    }

    @Test
    public void everydayAddAnnualDayTest() {
        when(userRepository.findNewByHireDate(any(LocalDate.class), anyInt())).thenReturn(newcomerList);

        myScheduleUtil.everydayAddAnnualDay();

        verify(userRepository, times(1)).findNewByHireDate(any(LocalDate.class), anyInt());
    }
}
