package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long userId);

    @Override
    List<User> findAll();

    @Override
    void deleteById(Long userId);

    @Query("SELECT DISTINCT u.email FROM User u")
    Set<String> findDistinctEmails();
}
