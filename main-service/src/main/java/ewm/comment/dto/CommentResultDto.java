package ewm.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ewm.user.dto.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResultDto {

    private Long id;
    private UserShortDto author;
    private String text;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private List<CommentResultChildDto> childComments;
    private boolean updated;
}
