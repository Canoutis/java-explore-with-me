package ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByEventIdIn(Collection<Long> ids);

    boolean existsByStatusAndIdIn(RequestStatus status, Collection<Long> ids);

    List<Request> findByRequesterId(int userId);

    long countByRequesterIdAndEventId(int userId, long eventId);

    int countByEventIdAndStatus(long eventId, RequestStatus status);

    List<Request> findByEventIdAndStatus(long eventId, RequestStatus eventState);

    @Transactional
    @Modifying
    @Query("update Request r set r.status = ?1 where r.id in ?2")
    int updateRequestStatusByIds(RequestStatus status, List<Long> ids);

    List<Request> findByIdIn(List<Long> ids);

    @Query("select r.event.id, COUNT(r.id) from Request r where r.event.id in ?1 and r.status = 'CONFIRMED' GROUP BY r.id")
    List<Object[]> countConfirmedRequestsByEventIds(Collection<Long> ids);


}
