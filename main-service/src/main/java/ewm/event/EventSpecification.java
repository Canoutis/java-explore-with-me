package ewm.event;

import ewm.request.Request;
import ewm.request.RequestStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ewm.utils.Helper.dateTimeFormatter;

public class EventSpecification {
    public static Specification<Event> hasUsers(List<Integer> users) {
        return (root, query, criteriaBuilder) -> root.join("initiator").get("id").in(users);
    }

    public static Specification<Event> hasStates(List<String> states) {
        List<EventState> eventStates = states.stream().map(EventState::valueOf).collect(Collectors.toList());
        return (root, query, criteriaBuilder) -> root.get("state").in(eventStates);
    }

    public static Specification<Event> hasCategories(List<Integer> categories) {
        return (root, query, criteriaBuilder) -> root.join("category").get("id").in(categories);
    }

    public static Specification<Event> searchText(String text) {
        return (root, query, criteriaBuilder) -> {
            String searchText = "%" + text.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), searchText),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchText)
            );
        };
    }

    public static Specification<Event> isPaid(Boolean paid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid);
    }

    public static Specification<Event> isAvailable() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Request> subRoot = subquery.from(Request.class);
            subquery.select(criteriaBuilder.count(subRoot));

            Predicate correlatedPredicate = criteriaBuilder.equal(subRoot.get("event"), root);
            correlatedPredicate = criteriaBuilder.and(correlatedPredicate, criteriaBuilder.equal(subRoot.get("status"), RequestStatus.CONFIRMED));

            subquery.where(correlatedPredicate);

            return criteriaBuilder.lessThan(subquery, root.get("participantLimit"));
        };
    }

    public static Specification<Event> published() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED);
    }

    public static Specification<Event> dateBetween(String rangeStart, String rangeEnd) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (rangeStart != null) {
                LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), startDateTime));
            }

            if (rangeEnd != null) {
                LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), endDateTime));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
