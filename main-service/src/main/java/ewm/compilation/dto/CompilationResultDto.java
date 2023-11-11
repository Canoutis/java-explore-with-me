package ewm.compilation.dto;

import ewm.event.dto.EventOutputDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationResultDto {
    Long id;
    List<EventOutputDto> events;
    Boolean pinned;
    String title;
}
