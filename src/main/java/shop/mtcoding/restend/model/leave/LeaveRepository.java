package shop.mtcoding.restend.model.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;

import java.time.LocalDate;
import java.util.List;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    @Query("select l from Leave l join fetch l.user where l.startDate = :today and l.status = :waiting")
    List<Leave> findByStartDateAndStatus(@Param("today") LocalDate today, @Param("waiting") LeaveStatus waiting);//@Param 없으면 오류남.
    
    List<Leave> findAllByUserId(Long userId);
    
}
