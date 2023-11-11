package ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationRequestDto {
    Long id;
    @Builder.Default
    List<Long> events = new ArrayList<>();
    @Builder.Default
    Boolean pinned = false;
    @Size(min = 1, max = 50)
    @NotBlank
    String title;
}
