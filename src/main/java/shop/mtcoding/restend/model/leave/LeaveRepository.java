package shop.mtcoding.restend.model.leave;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findAllByUserId(Long userId);
}
