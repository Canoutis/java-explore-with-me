package ewm.service;

import ewm.model.RequestHitDto;
import ewm.model.RequestHitInfoDto;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {
    RequestHitDto create(RequestHitDto requestHitDto);

    List<RequestHitInfoDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
