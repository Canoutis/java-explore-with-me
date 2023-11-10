package ewm.event;

import ewm.event.dto.EventInputDto;
import ewm.event.dto.EventOutputDto;
import ewm.event.dto.EventUpdateDto;
import ewm.request.dto.RequestDto;
import ewm.request.dto.RequestsUpdateInDto;
import ewm.request.dto.RequestsUpdateOutDto;

import java.util.List;

public interface EventService {
    EventOutputDto create(int userId, EventInputDto eventInputDto);

    List<EventOutputDto> findEvents(int userId, int from, int size);

    EventOutputDto findUserEventById(int userId, long eventId);

    EventOutputDto updateEventById(int userId, long eventId, EventUpdateDto eventUpdateDto);

    List<EventOutputDto> findEventsByFilters(List<Integer> users, List<String> states, List<Integer> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    List<EventOutputDto> findEventsByFilters(String text, List<Integer> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, EventState.Sort sort, Integer from, Integer size, String ip, String uri);

    EventOutputDto updateEventByIdByAdmin(long eventId, EventUpdateDto eventUpdateDto);

    EventOutputDto getEventById(long eventId, String ip, String uri);

    List<RequestDto> getEventRequests(int userId, long eventId);

    RequestsUpdateOutDto changeEventRequestsState(int userId, long eventId, RequestsUpdateInDto requestsUpdateInDto);
}
