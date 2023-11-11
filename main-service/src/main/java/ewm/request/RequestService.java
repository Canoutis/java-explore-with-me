package ewm.request;

import ewm.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> findUserRequests(int userId);

    RequestDto create(int userId, long eventId);

    RequestDto cancelRequestByOwner(int userId, long requestId);
}
