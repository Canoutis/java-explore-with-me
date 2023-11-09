package ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestsUpdateOutDto {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
