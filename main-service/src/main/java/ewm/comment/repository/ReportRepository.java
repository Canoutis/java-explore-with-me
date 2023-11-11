package ewm.comment.repository;

import ewm.comment.model.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByState(Report.State state, Pageable pageable);

    boolean existsByPersonIdAndCommentId(Integer id, Long id1);
}
