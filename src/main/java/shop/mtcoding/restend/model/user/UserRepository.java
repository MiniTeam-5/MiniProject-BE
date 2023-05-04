package shop.mtcoding.restend.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u inner join Leave l on u.id = l.user.id where where l.startDate = :today and l.status = :waiting")
    List<User> findByStartDateAndStatus(LocalDate today, LeaveStatus waiting);


}