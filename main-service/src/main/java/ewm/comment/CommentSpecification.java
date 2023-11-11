package ewm.comment;

import ewm.comment.model.Comment;
import ewm.comment.model.ModerationState;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ewm.utils.Helper.dateTimeFormatter;

public class CommentSpecification {

    public static Specification<Comment> hasState(ModerationState state) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), state);
    }

    public static Specification<Comment> hasEventIdIn(List<Long> eventIds) {
        return (root, query, criteriaBuilder) -> root.join("event").get("id").in(eventIds);
    }

    public static Specification<Comment> dateBetween(String rangeStart, String rangeEnd) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (rangeStart != null) {
                LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("created"), startDateTime));
            }

            if (rangeEnd != null) {
                LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("created"), endDateTime));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
