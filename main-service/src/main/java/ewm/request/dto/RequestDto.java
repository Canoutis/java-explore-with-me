package ewm.request.dto;

import ewm.request.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private Long id;
    private LocalDateTime created;
    private Long event;
    private Integer requester;
    private RequestStatus status;
}
