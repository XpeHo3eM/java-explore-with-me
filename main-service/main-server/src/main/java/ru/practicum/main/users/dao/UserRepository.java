package ru.practicum.main.users.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.users.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u" +
            " FROM User AS u" +
            " WHERE (:ids IS NULL OR u.id IN :ids)")
    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    boolean existsByEmail(String email);
}