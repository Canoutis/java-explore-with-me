package ewm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class RequestHitInfoDto {
    private String app;
    private String uri;
    private Long hits;
}
