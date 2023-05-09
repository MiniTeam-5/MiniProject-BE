package shop.mtcoding.restend.model.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;
import shop.mtcoding.restend.model.leave.enums.LeaveType;

import java.time.LocalDate;
import java.util.List;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    @Query("select l from Leave l join fetch l.user where l.startDate = :today and l.status = :waiting")
    List<Leave> findByStartDateAndStatus(@Param("today") LocalDate today, @Param("waiting") LeaveStatus waiting);//@Param 없으면 오류남.

    @Query("select case when count(l) > 0 then true else false end from Leave l where l.type = :duty " +
            "and l.startDate = :date and l.user.id = :id")
    boolean existsDuplicateDuty(@Param("duty") LeaveType type, @Param("date") LocalDate startDate, @Param("id") Long userId);

    @Query("select case when count(l) > 0 then true else false end from Leave l where l.type = :annual and l.user.id = :id " +
            "and ((l.startDate <= :start and l.endDate >= :start) or (l.startDate <= :end and l.endDate >= :end)" +
            "or (l.startDate >= :start and l.endDate <= :end))")
    boolean existsDuplicateAnnual(@Param("annual")LeaveType type, @Param("start") LocalDate startDate,
                                  @Param("end") LocalDate endDate, @Param("id") Long userId);

    List<Leave> findByStatus(LeaveStatus status);

    List<Leave> findAllByUserId(Long userId);
}

