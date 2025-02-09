package mate.academy.intro.repository;

import java.util.Optional;
import mate.academy.intro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
