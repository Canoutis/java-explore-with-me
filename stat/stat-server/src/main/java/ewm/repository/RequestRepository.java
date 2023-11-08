package ewm.repository;

import ewm.model.Request;
import ewm.model.RequestHitInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT new ewm.model.RequestHitInfoDto(s.app, s.uri, COUNT(s)) " +
            "FROM Request s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri")
    List<RequestHitInfoDto> findByDateAndUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ewm.model.RequestHitInfoDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Request s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri")
    List<RequestHitInfoDto> findByDateAndUriUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ewm.model.RequestHitInfoDto(s.app, s.uri, COUNT(s)) " +
            "FROM Request s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri")
    List<RequestHitInfoDto> findByDate(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ewm.model.RequestHitInfoDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Request s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri")
    List<RequestHitInfoDto> findByDateUnique(LocalDateTime start, LocalDateTime end);
}
