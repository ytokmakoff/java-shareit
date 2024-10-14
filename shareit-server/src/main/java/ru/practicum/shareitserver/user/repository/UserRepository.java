package ru.practicum.shareitserver.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareitserver.user.model.User;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT DISTINCT u.email FROM User u")
    Set<String> findDistinctEmails();
}
