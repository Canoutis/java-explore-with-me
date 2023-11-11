package ewm.comment;

import ewm.comment.dto.BlackListDto;
import ewm.comment.dto.CommentRequestDto;
import ewm.comment.dto.CommentResultChildDto;
import ewm.comment.dto.CommentResultDto;
import ewm.comment.dto.CommentShortResultDto;
import ewm.comment.dto.ReportDto;
import ewm.comment.model.ModerationState;
import ewm.comment.model.Report;
import ewm.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(value = "/users/{userId}/events/{eventId}/comments")
    public ResponseEntity<CommentShortResultDto> addComment(@PathVariable Integer userId, @PathVariable Long eventId,
                                                            @RequestBody CommentRequestDto commentRequest) {
        return new ResponseEntity<>(commentService.addComment(userId, eventId, commentRequest), HttpStatus.CREATED);
    }

    @GetMapping(value = "/events/{eventId}/comments")
    public ResponseEntity<List<CommentResultDto>> getComments(@PathVariable Long eventId,
                                                              @RequestParam(defaultValue = "0", required = false) Integer from,
                                                              @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(commentService.getComments(eventId, from, size), HttpStatus.OK);
    }

    @PostMapping(value = "/users/{userId}/events/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentShortResultDto> addComment(@PathVariable Integer userId, @PathVariable Long eventId,
                                                            @PathVariable Long commentId,
                                                            @RequestBody CommentRequestDto commentRequest) {
        return new ResponseEntity<>(commentService.addComment(userId, eventId, commentId, commentRequest), HttpStatus.CREATED);
    }

    @PostMapping(value = "/users/{userId}/comments/{commentId}/report")
    public ResponseEntity<ReportDto> reportComment(@PathVariable Integer userId, @PathVariable Long commentId) {
        commentService.reportComment(userId, commentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/comments/{commentId}")
    public ResponseEntity<CommentResultDto> getComment(@PathVariable Long commentId) {
        return new ResponseEntity<>(commentService.getComment(commentId), HttpStatus.OK);
    }

    @GetMapping(value = "/comments/{commentId}/children")
    public ResponseEntity<List<CommentResultChildDto>> getChildComments(@PathVariable Long commentId,
                                                                        @RequestParam(defaultValue = "0", required = false) Integer from,
                                                                        @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(commentService.getChildComments(commentId, from, size), HttpStatus.OK);
    }

    @PatchMapping(value = "/users/{userId}/comments/{commentId}")
    public ResponseEntity<CommentShortResultDto> updateComment(@PathVariable Integer userId, @PathVariable Long commentId,
                                                               @RequestBody CommentRequestDto commentRequest) {
        return new ResponseEntity<>(commentService.updateComment(userId, commentId, commentRequest), HttpStatus.OK);
    }

    @DeleteMapping(value = "/users/{userId}/comments/{commentId}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable Integer userId, @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/admin/comments")
    public ResponseEntity<List<CommentResultDto>> getComments(@RequestParam(defaultValue = "PUBLISHED", required = false) ModerationState state,
                                                              @RequestParam(required = false) List<Long> eventIds,
                                                              @RequestParam(required = false) String rangeStart,
                                                              @RequestParam(required = false) String rangeEnd,
                                                              @RequestParam(defaultValue = "0", required = false) Integer from,
                                                              @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(commentService.getCommentsAdmin(state, eventIds, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping(value = "/admin/comments")
    public ResponseEntity<List<CommentResultDto>> changeCommentsStatuses(@RequestParam(required = false) List<Long> commentIds,
                                                                         @RequestParam(defaultValue = "DELETED", required = false) ModerationState state) {
        return new ResponseEntity<>(commentService.changeCommentsStatuses(commentIds, state), HttpStatus.OK);
    }

    @GetMapping(value = "/admin/comments/reports")
    public ResponseEntity<List<ReportDto>> getReports(@RequestParam(defaultValue = "PENDING", required = false) Report.State state,
                                                      @RequestParam(defaultValue = "0", required = false) Integer from,
                                                      @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(commentService.getReports(state, from, size), HttpStatus.OK);
    }

    @PatchMapping(value = "/admin/comments/reports/{reportId}")
    public ResponseEntity<CommentResultDto> handleCommentReport(@PathVariable Long reportId,
                                                                @RequestParam Report.State state) {
        return new ResponseEntity<>(commentService.handleCommentReport(reportId, state), HttpStatus.OK);
    }

    @PostMapping(value = "/admin/users/{userId}/comments/{commentId}/block")
    public ResponseEntity<BlackListDto> blockUser(@PathVariable Integer userId,
                                                  @PathVariable Long commentId) {
        return new ResponseEntity<>(commentService.blockUser(userId, commentId), HttpStatus.CREATED);
    }

}
