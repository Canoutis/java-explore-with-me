package ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAll(Specification<Event> specification, Pageable page);

    @Transactional
    @Modifying
    @Query("update Event e set e.views = ?1 where e.id = ?2")
    void updateViewsById(long views, Long id);

}
