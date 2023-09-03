package ru.practicum.stats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.dto.HitsStatDto;
import ru.practicum.stats.model.StatItem;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<StatItem, Long> {
    @Query("SELECT new ru.practicum.stats.dto.HitsStatDto (" +
            "    si.app," +
            "    si.uri," +
            "    CASE WHEN :unique = TRUE" +
            "        THEN COUNT(DISTINCT(si.ip))" +
            "        ELSE COUNT(si.ip)" +
            "    END AS countIp" +
            " )" +
            " FROM StatItem si " +
            " WHERE si.timestamp BETWEEN :start AND :end" +
            "    AND (COALESCE(:uris, NULL) IS NULL OR si.uri IN :uris)" +
            " GROUP BY si.app, si.uri" +
            " ORDER BY countIp DESC")
    List<HitsStatDto> getStats(LocalDateTime start,
                               LocalDateTime end,
                               List<String> uris,
                               Boolean unique);
}
