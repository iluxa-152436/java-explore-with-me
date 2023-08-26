package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.entity.Hit;
import ru.practicum.explorewithme.entity.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitStorage extends JpaRepository<Hit, Long> {
    @Query("SELECT " +
            "h.app AS app, " +
            "h.uri AS uri, " +
            "COUNT(h.ip) AS hits " +
            "FROM Hit AS h " +
            "WHERE h.timeStamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits desc")
    List<Stats> findStat(LocalDateTime start, LocalDateTime end);

    @Query("SELECT " +
            "h.app AS app, " +
            "h.uri AS uri, " +
            "COUNT(DISTINCT(h.ip)) AS hits " +
            "FROM Hit AS h " +
            "WHERE h.timeStamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits desc")
    List<Stats> findUniqueStat(LocalDateTime start, LocalDateTime end);

    @Query("SELECT " +
            "h.app AS app, " +
            "h.uri AS uri, " +
            "COUNT(DISTINCT(h.ip)) AS hits " +
            "FROM Hit AS h " +
            "WHERE (h.timeStamp BETWEEN :start AND :end) AND (uri IN (:uris)) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits desc")
    List<Stats> findUniqueStatByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT " +
            "h.app AS app, " +
            "h.uri AS uri, " +
            "COUNT(h.ip) AS hits " +
            "FROM Hit AS h " +
            "WHERE (h.timeStamp BETWEEN :start AND :end) AND (uri IN (:uris)) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits desc")
    List<Stats> findStatByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
