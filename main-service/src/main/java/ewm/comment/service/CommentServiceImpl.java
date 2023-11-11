package ewm.comment.service;

import ewm.comment.CommentMapper;
import ewm.comment.CommentSpecification;
import ewm.comment.dto.BlackListDto;
import ewm.comment.dto.CommentRequestDto;
import ewm.comment.dto.CommentResultChildDto;
import ewm.comment.dto.CommentResultDto;
import ewm.comment.dto.CommentShortResultDto;
import ewm.comment.dto.ReportDto;
import ewm.comment.model.BlackList;
import ewm.comment.model.Comment;
import ewm.comment.model.ModerationState;
import ewm.comment.model.Report;
import ewm.comment.repository.BlackListRepository;
import ewm.comment.repository.CommentRepository;
import ewm.comment.repository.ReportRepository;
import ewm.event.Event;
import ewm.event.EventRepository;
import ewm.exception.BadRequestException;
import ewm.exception.ObjectAccessException;
import ewm.exception.ObjectNotFoundException;
import ewm.exception.ObjectSaveException;
import ewm.user.User;
import ewm.user.UserMapper;
import ewm.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ewm.comment.model.ModerationState.PUBLISHED;
import static ewm.utils.Helper.findEventById;
import static ewm.utils.Helper.findUserById;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BlackListRepository blackListRepository;
    private final ReportRepository reportRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository,
                              BlackListRepository blackListRepository,
                              ReportRepository reportRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.blackListRepository = blackListRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public CommentShortResultDto addComment(Integer userId, Long eventId, CommentRequestDto commentRequest) {
        return addComment(userId, eventId, null, commentRequest);
    }

    @Override
    public CommentShortResultDto addComment(Integer userId, Long eventId, Long parentCommentId, CommentRequestDto commentRequest) {
        User author = findUserById(userRepository, userId);
        if (blackListRepository.existsByPersonId(userId)) {
            throw new ObjectAccessException("Вам запрещен функционал комментирования событий.");
        }
        if (commentRepository.existsByAuthorIdAndEventIdAndText(userId, eventId, commentRequest.getText())) {
            throw new ObjectSaveException("Нельзя спамить одинаковые комментарии! Можно получить бан!");
        }
        Comment parentComment = parentCommentId == null ? null : findCommentById(parentCommentId);
        if (parentComment != null && parentComment.getParentComment() != null) {
            throw new BadRequestException("Допустим только 1 уровень подкомментирования.");
        }
        Event event = findEventById(eventRepository, eventId);

        return CommentMapper.toShortDto(
                commentRepository.save(
                        CommentMapper.toEntity(
                                commentRequest, event, author, parentComment)));
    }

    @Override
    public CommentShortResultDto updateComment(Integer userId, Long commentId, CommentRequestDto commentRequest) {
        findUserById(userRepository, userId);
        Comment comment = findCommentById(commentId);
        if (!comment.getAuthor().getId().equals(userId) ||
                blackListRepository.existsByPersonId(userId) ||
                comment.getState() == ModerationState.DELETED) {
            throw new ObjectAccessException("У вас нет прав для изменения этого комментария.");
        }

        boolean needUpdate = false;
        if (commentRequest.getAnonymous() != null && comment.isAnonymous() != commentRequest.getAnonymous()) {
            comment.setAnonymous(commentRequest.getAnonymous());
            needUpdate = true;
        }
        if (commentRequest.getText() != null && !comment.getText().equals(commentRequest.getText())) {
            comment.setText(commentRequest.getText());
            needUpdate = true;
        }
        if (needUpdate) {
            comment.setUpdated(true);
            return CommentMapper.toShortDto(commentRepository.save(comment));
        }
        return CommentMapper.toShortDto(comment);
    }

    @Override
    public void deleteComment(Integer userId, Long commentId) {
        findUserById(userRepository, userId);
        Comment comment = findCommentById(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ObjectAccessException("У вас нет прав для удаление этого комментария.");
        }
        comment.setState(ModerationState.DELETED);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentResultDto> getComments(Long eventId, int from, int size) {
        findEventById(eventRepository, eventId);
        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, "created"));
        List<Comment> comments = commentRepository.findByEventId(eventId, PUBLISHED, pageable);
        return loadCommentChildrenAndMap(comments);
    }

    @Override
    public List<CommentResultChildDto> getChildComments(Long commentId, int from, int size) {
        return commentRepository
                .findByParentCommentIdInAndState(Collections.singletonList(commentId), PUBLISHED,
                        Sort.by(Sort.Direction.ASC, "created"))
                .stream().map(CommentMapper::toChildDto).collect(Collectors.toList());
    }

    @Override
    public CommentResultDto getComment(Long commentId) {
        Comment comment = findCommentById(commentId);
        List<CommentResultChildDto> children = commentRepository
                .findByParentCommentIdInAndState(Collections.singletonList(commentId), PUBLISHED,
                        Sort.by(Sort.Direction.ASC, "created"))
                .stream().map(CommentMapper::toChildDto).collect(Collectors.toList());
        return CommentMapper.toDto(comment, children);
    }

    @Override
    public void reportComment(Integer userId, Long commentId) {
        User user = findUserById(userRepository, userId);
        Comment comment = findCommentById(commentId);
        if (reportRepository.existsByPersonIdAndCommentId(userId, commentId)) {
            throw new ObjectSaveException("Вы уже оставили жалобу. Спасибо за бдительность!");
        }
        Report report = Report.builder()
                .person(user)
                .comment(comment)
                .state(Report.State.PENDING)
                .build();
        reportRepository.save(report);
    }

    @Override
    public List<CommentResultDto> getCommentsAdmin(ModerationState state, List<Long> eventIds,
                                                   String rangeStart, String rangeEnd,
                                                   int from, int size) {
        Specification<Comment> spec = Specification.where(CommentSpecification.hasState(state));
        if (eventIds != null) {
            spec.and(CommentSpecification.hasEventIdIn(eventIds));
        }
        if (rangeStart != null || rangeEnd != null) {
            spec.and(CommentSpecification.dateBetween(rangeStart, rangeEnd));
        }

        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Comment> comments = commentRepository.findAll(spec, pageable);

        return comments.stream().map(comment -> CommentMapper.toDto(comment, new ArrayList<>())).collect(Collectors.toList());
    }

    @Override
    public List<ReportDto> getReports(Report.State state, int from, int size) {
        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Report> reports = reportRepository.findByState(state, pageable);
        return reports.stream().map(report ->
                        new ReportDto(report.getId(), UserMapper.toDto(report.getPerson()),
                                CommentMapper.toDto(report.getComment(), null), report.getCreated()))
                .collect(Collectors.toList());
    }

    @Override
    public CommentResultDto handleCommentReport(Long reportId, Report.State state) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isEmpty() || reportOptional.get().getState() == Report.State.PROCESSED) {
            throw new ObjectNotFoundException("Жалобу не удалось найти, или она уже обработана.");
        }
        Report report = reportOptional.get();
        Comment comment = report.getComment();
        if (state == Report.State.DELETE_COMMENT) {
            comment.setState(ModerationState.DELETED);
            comment = commentRepository.save(comment);
        }
        report.setState(Report.State.PROCESSED);
        reportRepository.save(report);
        return loadCommentChildrenAndMap(Collections.singletonList(comment)).get(0);
    }

    @Override
    public List<CommentResultDto> changeCommentsStatuses(List<Long> commentIds, ModerationState state) {
        commentRepository.updateStateByIdIn(state, commentIds);
        List<Comment> comments = commentRepository.findByIdIn(commentIds, Sort.by(Sort.Direction.ASC, "created"));
        return comments.stream().map(comment -> CommentMapper.toDto(comment, new ArrayList<>())).collect(Collectors.toList());
    }

    @Override
    public BlackListDto blockUser(Integer userId, Long commentId) {
        User user = findUserById(userRepository, userId);
        Comment comment = findCommentById(commentId);
        if (blackListRepository.existsByPersonId(userId)) {
            throw new ObjectSaveException("Пользователь уже заблокирован.");
        }
        return CommentMapper.toDto(blackListRepository.save(BlackList.builder().person(user).reason(comment).build()));
    }

    private Comment findCommentById(Long commentId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty() ||
                (commentOptional.get().getState() == ModerationState.DELETED &&
                        !commentRepository.existsByParentCommentIdAndState(commentOptional.get().getId(), PUBLISHED))) {
            throw new ObjectNotFoundException(String.format("Комментарий с id=%d не найден!", commentId));
        }
        return commentOptional.get();
    }

    private List<CommentResultDto> loadCommentChildrenAndMap(List<Comment> comments) {
        List<Comment> childComments = commentRepository.findByParentCommentIdInAndState(
                comments.stream().map(Comment::getId).collect(Collectors.toList()), PUBLISHED, Sort.by(Sort.Direction.ASC, "created"));
        HashMap<Long, List<CommentResultChildDto>> commentIdAndChildren = new HashMap<>();
        for (Comment comment : childComments) {
            List<CommentResultChildDto> children = commentIdAndChildren
                    .getOrDefault(comment.getParentComment().getId(), new ArrayList<>());
            children.add(CommentMapper.toChildDto(comment));
            commentIdAndChildren.put(comment.getParentComment().getId(), children);
        }
        return comments.stream()
                .map(comment -> CommentMapper.toDto(comment, commentIdAndChildren.getOrDefault(comment.getId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }

}
