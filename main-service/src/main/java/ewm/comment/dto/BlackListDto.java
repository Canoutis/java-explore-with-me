package ewm.comment.dto;

import ewm.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlackListDto {
    private Integer id;
    private UserDto person;
    private CommentShortResultDto reason;
}
