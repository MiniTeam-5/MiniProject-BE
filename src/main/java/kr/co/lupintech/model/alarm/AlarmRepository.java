package kr.co.lupintech.model.alarm;

import kr.co.lupintech.model.leave.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("SELECT a FROM Alarm a WHERE a.user.id = :userId")
    List<Alarm> findByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Alarm a WHERE a.user.id = :userId AND a.leave.status = :status")
    List<Alarm> findByUserIdAndLeaveStatus(@Param("userId") Long userId, @Param("status") LeaveStatus status);

    @Query("SELECT a FROM Alarm a WHERE a.leave.status = :status")
    List<Alarm> findByLeaveStatus(@Param("status") LeaveStatus status);

}