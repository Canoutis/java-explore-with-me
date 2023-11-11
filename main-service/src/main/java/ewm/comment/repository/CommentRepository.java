package ewm.comment.repository;

import ewm.comment.model.Comment;
import ewm.comment.model.ModerationState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByParentCommentIdAndState(Long id, ModerationState state);

    boolean existsByAuthorIdAndEventIdAndText(Integer userId, Long eventId, String text);

    List<Comment> findByIdIn(Collection<Long> ids, Sort sort);

    @Modifying
    @Query("update Comment c set c.state = ?1 where c.id in ?2")
    void updateStateByIdIn(ModerationState state, Collection<Long> ids);

    List<Comment> findByParentCommentIdInAndState(Collection<Long> ids, ModerationState state, Sort sort);

    List<Comment> findByEventId(Long id, ModerationState state, Pageable pageable);

    List<Comment> findAll(Specification<Comment> spec, Pageable pageable);
}
