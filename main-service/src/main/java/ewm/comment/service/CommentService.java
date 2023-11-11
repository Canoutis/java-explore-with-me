package ewm.comment.service;

import ewm.comment.dto.BlackListDto;
import ewm.comment.dto.CommentRequestDto;
import ewm.comment.dto.CommentResultChildDto;
import ewm.comment.dto.CommentResultDto;
import ewm.comment.dto.CommentShortResultDto;
import ewm.comment.dto.ReportDto;
import ewm.comment.model.ModerationState;
import ewm.comment.model.Report;

import java.util.List;

public interface CommentService {
    CommentShortResultDto addComment(Integer userId, Long eventId, CommentRequestDto commentRequest);

    CommentShortResultDto addComment(Integer userId, Long eventId, Long parentCommentId, CommentRequestDto commentRequest);

    CommentShortResultDto updateComment(Integer userId, Long commentId, CommentRequestDto commentRequest);

    void deleteComment(Integer userId, Long commentId);

    List<CommentResultDto> getComments(Long eventId, int from, int size);

    List<CommentResultChildDto> getChildComments(Long commentId, int from, int size);

    CommentResultDto getComment(Long commentId);

    void reportComment(Integer userId, Long commentId);

    List<CommentResultDto> getCommentsAdmin(ModerationState state, List<Long> eventIds, String rangeStart, String rangeEnd, int from, int size);

    List<ReportDto> getReports(Report.State state, int from, int size);

    CommentResultDto handleCommentReport(Long reportId, Report.State state);

    List<CommentResultDto> changeCommentsStatuses(List<Long> commentIds, ModerationState state);

    BlackListDto blockUser(Integer userId, Long commentId);
}
