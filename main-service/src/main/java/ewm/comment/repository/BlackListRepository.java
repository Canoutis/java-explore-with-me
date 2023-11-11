package ewm.comment.repository;

import ewm.comment.model.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<BlackList, Integer> {
    boolean existsByPersonId(Integer id);
}
