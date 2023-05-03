package shop.mtcoding.restend.model.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mtcoding.restend.model.leave.Leave;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}