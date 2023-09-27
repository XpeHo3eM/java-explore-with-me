package ru.practicum.main.compilations.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c" +
            " FROM Compilation AS c" +
            " WHERE (:pinned IS NULL OR c.pinned = :pinned)")
    Page<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);
}