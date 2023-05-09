package shop.mtcoding.restend.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import shop.mtcoding.restend.model.leave.enums.LeaveStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u where u.status = true " +
            "and u.hireDate <= :date and month(u.hireDate) = :month and day(u.hireDate) = :day")
    List<User> findByHireDate(@Param("date") LocalDate date, @Param("month") int month, @Param("day") int day);

    @Query("select u from User u where u.status = true " +
            "and u.hireDate > :date and day(u.hireDate) = :day")
    List<User> findNewByHireDate(@Param("date") LocalDate date, @Param("day") int day);

    @Query("select u from User u where u.role IN :roles")
    List<User> findByRoles(@Param("roles") Set<UserRole> roles);
}