package ewm.event;

import ewm.event.dto.EventInputDto;
import ewm.event.dto.EventOutputDto;
import ewm.event.dto.EventUpdateDto;
import ewm.request.dto.RequestDto;
import ewm.request.dto.RequestsUpdateInDto;
import ewm.request.dto.RequestsUpdateOutDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(value = "/users/{userId}/events")
    public ResponseEntity<EventOutputDto> create(@PathVariable Integer userId, @Valid @RequestBody EventInputDto eventInputDto) {
        return new ResponseEntity<>(eventService.create(userId, eventInputDto), HttpStatus.CREATED);
    }

    @GetMapping(value = "/users/{userId}/events")
    public ResponseEntity<List<EventOutputDto>> findEvents(@PathVariable Integer userId,
                                                           @RequestParam(defaultValue = "0", required = false) Integer from,
                                                           @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(eventService.findEvents(userId, from, size), HttpStatus.OK);
    }

    @GetMapping(value = "/users/{userId}/events/{eventId}")
    public ResponseEntity<EventOutputDto> findEventById(@PathVariable Integer userId, @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.findUserEventById(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}")
    public ResponseEntity<EventOutputDto> updateEventById(@PathVariable Integer userId, @PathVariable Long eventId,
                                                          @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        return new ResponseEntity<>(eventService.updateEventById(userId, eventId, eventUpdateDto), HttpStatus.OK);
    }

    @GetMapping(value = "/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<RequestDto>> getEventRequests(@PathVariable Integer userId, @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.getEventRequests(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping(value = "/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<RequestsUpdateOutDto> changeEventRequestsState(@PathVariable Integer userId, @PathVariable Long eventId,
                                                                         @RequestBody RequestsUpdateInDto requestsUpdateInDto) {
        RequestsUpdateOutDto result = eventService.changeEventRequestsState(userId, eventId, requestsUpdateInDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/admin/events")
    public ResponseEntity<List<EventOutputDto>> findEventsByFilters(@RequestParam(required = false) List<Integer> users,
                                                                    @RequestParam(required = false) List<String> states,
                                                                    @RequestParam(required = false) List<Integer> categories,
                                                                    @RequestParam(required = false) String rangeStart,
                                                                    @RequestParam(required = false) String rangeEnd,
                                                                    @RequestParam(defaultValue = "0", required = false) Integer from,
                                                                    @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(eventService.findEventsByFilters(users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping(value = "/admin/events/{eventId}")
    public ResponseEntity<EventOutputDto> updateEventByIdByAdmin(@PathVariable Long eventId,
                                                                 @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        return new ResponseEntity<>(eventService.updateEventByIdByAdmin(eventId, eventUpdateDto), HttpStatus.OK);
    }

    @GetMapping(value = "/events")
    public ResponseEntity<List<EventOutputDto>> findEventsByFilters(@RequestParam(required = false) String text,
                                                                    @RequestParam(required = false) List<Integer> categories,
                                                                    @RequestParam(required = false) Boolean paid,
                                                                    @RequestParam(required = false) String rangeStart,
                                                                    @RequestParam(required = false) String rangeEnd,
                                                                    @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                                    @RequestParam(required = false) String sort,
                                                                    @RequestParam(defaultValue = "0", required = false) Integer from,
                                                                    @RequestParam(defaultValue = "10", required = false) Integer size,
                                                                    HttpServletRequest req) {
        return new ResponseEntity<>(eventService.findEventsByFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, req.getRemoteAddr(), req.getRequestURI()), HttpStatus.OK);
    }

    @GetMapping(value = "/events/{id}")
    public ResponseEntity<EventOutputDto> getEventById(@PathVariable Long id, HttpServletRequest req) {
        return new ResponseEntity<>(eventService.getEventById(id, req.getRemoteAddr(), req.getRequestURI()), HttpStatus.OK);
    }

}
