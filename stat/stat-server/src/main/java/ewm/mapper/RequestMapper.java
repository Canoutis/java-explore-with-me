package ewm.mapper;

import ewm.model.Request;
import ewm.model.RequestHitDto;

public class RequestMapper {
    public static RequestHitDto entityToDto(Request entity) {
        return new RequestHitDto(entity.getApp(), entity.getUri(), entity.getIp(), entity.getTimestamp());
    }

    public static Request dtoToEntity(RequestHitDto dto) {
        return Request.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}
