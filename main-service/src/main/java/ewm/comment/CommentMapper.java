package ewm.comment;

import ewm.comment.dto.BlackListDto;
import ewm.comment.dto.CommentRequestDto;
import ewm.comment.dto.CommentResultChildDto;
import ewm.comment.dto.CommentResultDto;
import ewm.comment.dto.CommentShortResultDto;
import ewm.comment.model.BlackList;
import ewm.comment.model.Comment;
import ewm.comment.model.ModerationState;
import ewm.event.Event;
import ewm.user.User;
import ewm.user.UserMapper;

import java.util.List;

import static ewm.utils.Helper.ANONYMOUS;
import static ewm.utils.Helper.DELETED_COMMENT;

public class CommentMapper {

    public static Comment toEntity(CommentRequestDto commentRequest, Event event, User author, Comment parent) {
        return Comment.builder()
                .event(event)
                .author(author)
                .text(commentRequest.getText())
                .state(ModerationState.PUBLISHED)
                .parentComment(parent)
                .anonymous(commentRequest.getAnonymous())
                .build();
    }

    public static CommentResultDto toDto(Comment comment, List<CommentResultChildDto> children) {
        return CommentResultDto.builder()
                .id(comment.getId())
                .author(UserMapper.toShortDto(comment.isAnonymous() ||
                        comment.getState() == ModerationState.DELETED ? ANONYMOUS : comment.getAuthor()))
                .text(comment.getState() == ModerationState.DELETED ? DELETED_COMMENT : comment.getText())
                .created(comment.getCreated())
                .childComments(children)
                .updated(comment.isUpdated())
                .build();

    }

    public static CommentResultChildDto toChildDto(Comment comment) {
        return CommentResultChildDto.builder()
                .id(comment.getId())
                .author(UserMapper.toShortDto(comment.isAnonymous() ||
                        comment.getState() == ModerationState.DELETED ? ANONYMOUS : comment.getAuthor()))
                .text(comment.getState() == ModerationState.DELETED ? DELETED_COMMENT : comment.getText())
                .created(comment.getCreated())
                .updated(comment.isUpdated())
                .build();

    }

    public static CommentShortResultDto toShortDto(Comment comment) {
        return CommentShortResultDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .authorId(comment.isAnonymous() ||
                        comment.getState() == ModerationState.DELETED ? ANONYMOUS.getId() : comment.getAuthor().getId())
                .text(comment.getState() == ModerationState.DELETED ? DELETED_COMMENT : comment.getText())
                .created(comment.getCreated())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .updated(comment.isUpdated())
                .build();
    }

    public static BlackListDto toDto(BlackList blackList) {
        return BlackListDto.builder()
                .id(blackList.getId())
                .person(UserMapper.toDto(blackList.getPerson()))
                .reason(CommentMapper.toShortDto(blackList.getReason()))
                .build();
    }

}
