package ewm.request.dto;

import ewm.request.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestsUpdateInDto {

    List<Long> requestIds;
    RequestStatus status;
}
