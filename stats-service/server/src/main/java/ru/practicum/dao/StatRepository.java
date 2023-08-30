package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.HitsStatDto;
import ru.practicum.model.StatItem;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<StatItem, Long> {
    @Query("SELECT new ru.practicum.dto.HitsStatDto (" +
            "    si.app," +
            "    si.uri," +
            "    CASE WHEN :unique = TRUE" +
            "        THEN COUNT(DISTINCT(si.ip))" +
            "        ELSE COUNT(si.ip)" +
            "    END AS countIp" +
            " )" +
            " FROM StatItem si " +
            " WHERE si.timestamp BETWEEN :start AND :end" +
            "    AND si.uri IN :uris" +
            " GROUP BY si.app, si.uri" +
            " ORDER BY countIp DESC")
    List<HitsStatDto> getStats(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end,
                               @Param("uris") List<String> uris,
                               @Param("unique") boolean unique);
}
