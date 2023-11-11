package ewm.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Location {
    private double lat;
    private double lon;
}
