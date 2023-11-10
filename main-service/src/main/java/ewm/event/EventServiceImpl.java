package ewm.event;

import ewm.category.Category;
import ewm.category.CategoryRepository;
import ewm.client.StatClient;
import ewm.event.dto.EventInputDto;
import ewm.event.dto.EventOutputDto;
import ewm.event.dto.EventUpdateDto;
import ewm.exception.BadRequestException;
import ewm.exception.ConflictRequestException;
import ewm.exception.ObjectNotFoundException;
import ewm.model.RequestHitDto;
import ewm.model.RequestHitInfoDto;
import ewm.request.Request;
import ewm.request.RequestMapper;
import ewm.request.RequestRepository;
import ewm.request.RequestStatus;
import ewm.request.dto.RequestDto;
import ewm.request.dto.RequestsUpdateInDto;
import ewm.request.dto.RequestsUpdateOutDto;
import ewm.user.User;
import ewm.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static ewm.utils.Helper.dateTimeFormatter;
import static ewm.utils.Helper.findCategoryById;
import static ewm.utils.Helper.findEventById;
import static ewm.utils.Helper.findUserById;

@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                            CategoryRepository categoryRepository, RequestRepository requestRepository,
                            StatClient statClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.statClient = statClient;
    }

    @Override
    @Transactional
    public EventOutputDto create(int userId, EventInputDto eventInputDto) {
        User initiator = findUserById(userRepository, userId);
        Category category = findCategoryById(categoryRepository, eventInputDto.getCategory());
        if (!eventInputDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Неподходящая дата события!");
        }
        Event event = EventMapper.toEntity(eventInputDto, category, initiator, EventState.PENDING);
        return EventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public List<EventOutputDto> findEvents(int userId, int from, int size) {
        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        Page<Event> events = eventRepository.findAll(pageable);
        return events.stream().map(EventMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EventOutputDto findUserEventById(int userId, long eventId) {
        Event event = findEventById(eventRepository, eventId);
        if (event.getInitiator().getId() != userId) {
            throw new ObjectNotFoundException(String.format("Событие с id=%d не найден!", eventId));
        } else {
            return EventMapper.toDto(event);
        }
    }

    @Override
    @Transactional
    public EventOutputDto updateEventById(int userId, long eventId, EventUpdateDto eventUpdateDto) {
        Event event = findEventById(eventRepository, eventId);
        if (event.getInitiator().getId() != userId) {
            throw new ObjectNotFoundException(String.format("Событие с id=%d не найден!", eventId));
        } else if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictRequestException("Это событие не может быть изменено!");
        } else {
            if (eventUpdateDto.getStateAction() != null) {
                if (eventUpdateDto.getStateAction() == EventState.StateAction.CANCEL_REVIEW)
                    event.setState(EventState.CANCELED);
                if (eventUpdateDto.getStateAction() == EventState.StateAction.SEND_TO_REVIEW)
                    event.setState(EventState.PENDING);
            }
            updateEventByEventUpdateDto(event, eventUpdateDto);
            return EventMapper.toDto(eventRepository.save(event));
        }
    }

    @Override
    public List<EventOutputDto> findEventsByFilters(List<Integer> users, List<String> states,
                                                    List<Integer> categories, String rangeStart,
                                                    String rangeEnd, Integer from, Integer size) {
        Specification<Event> spec = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            spec = spec.and(EventSpecification.hasUsers(users));
        }

        if (states != null && !states.isEmpty()) {
            spec = spec.and(EventSpecification.hasStates(states));
        }

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(EventSpecification.hasCategories(categories));
        }

        if (rangeStart != null || rangeEnd != null) {
            spec = spec.and(EventSpecification.dateBetween(rangeStart, rangeEnd));
        }
        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events = eventRepository.findAll(spec, pageable);

        return loadEventHitsAndRequestsAndMap(events, "/events");
    }

    @Override
    public List<EventOutputDto> findEventsByFilters(String text, List<Integer> categories, Boolean paid,
                                                    String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                    String sort, Integer from, Integer size, String ip, String uri) {
        RequestHitDto requestHitDto = RequestHitDto.builder()
                .app("ewm")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build();
        statClient.hitRequest(requestHitDto);

        Specification<Event> spec = Specification.where(EventSpecification.published());

        if (text != null && !text.isEmpty()) {
            spec = spec.and(EventSpecification.searchText(text));
        }

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(EventSpecification.hasCategories(categories));
        }

        if (paid != null) {
            spec = spec.and(EventSpecification.isPaid(paid));
        }

        if (onlyAvailable != null && onlyAvailable) {
            spec = spec.and(EventSpecification.isAvailable());
        }

        if (rangeStart == null) rangeStart = LocalDateTime.now().format(dateTimeFormatter);
        else if (rangeEnd != null && LocalDateTime.parse(rangeStart, dateTimeFormatter).isAfter(LocalDateTime.parse(rangeEnd, dateTimeFormatter))) {
            throw new BadRequestException("Некорректные входные параметры фильтра!");
        }
        spec = spec.and(EventSpecification.dateBetween(rangeStart, rangeEnd));

        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        if (sort != null && sort.equals("EVENT_DATE"))
            pageable = pageable.withSort(Sort.by(Sort.Direction.DESC, "eventDate"));

        List<EventOutputDto> events = loadEventHitsAndRequestsAndMap(eventRepository.findAll(spec, pageable), uri);

        if (sort != null && sort.equals("VIEWS"))
            events = events.stream()
                    .sorted(Comparator.comparingLong(EventOutputDto::getId).reversed()).collect(Collectors.toList());

        return events;
    }

    @Override
    @Transactional
    public EventOutputDto updateEventByIdByAdmin(long eventId, EventUpdateDto eventUpdateDto) {
        Event event = findEventById(eventRepository, eventId);
        if (LocalDateTime.now().plusHours(1).isAfter(event.getEventDate()) ||
                (event.getState() != EventState.PENDING && eventUpdateDto.getStateAction() == EventState.StateAction.PUBLISH_EVENT) ||
                (event.getState() == EventState.PUBLISHED && eventUpdateDto.getStateAction() == EventState.StateAction.REJECT_EVENT)) {
            throw new ConflictRequestException("Событие не удовлетворяет правилам редактирования");
        } else {
            updateEventByEventUpdateDto(event, eventUpdateDto);
            if (eventUpdateDto.getStateAction() == EventState.StateAction.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedTime(LocalDateTime.now());
            } else if (eventUpdateDto.getStateAction() == EventState.StateAction.REJECT_EVENT) {
                event.setState(EventState.CANCELED);
            } else {
                event.setState(event.getState());
            }
            return EventMapper.toDto(eventRepository.save(event));
        }
    }

    @Override
    public EventOutputDto getEventById(long eventId, String ip, String uri) {
        RequestHitDto requestHitDto = RequestHitDto.builder()
                .app("ewm")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build();
        Event event = findEventById(eventRepository, eventId);
        statClient.hitRequest(requestHitDto);
        if (event.getState() == EventState.PUBLISHED) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            EventOutputDto eventOutputDto = EventMapper.toDto(event);
            eventOutputDto.setConfirmedRequests(confirmedCount);
            eventOutputDto.setViews(statClient.getRequestHitInfoDto(eventOutputDto.getCreatedOn(),
                    LocalDateTime.now(), Collections.singletonList(uri), true).get(0).getHits());
            return eventOutputDto;
        } else {
            throw new ObjectNotFoundException(String.format("Событие с id=%d не найдено!", eventId));
        }
    }

    @Override
    public List<RequestDto> getEventRequests(int userId, long eventId) {
        findUserById(userRepository, userId);
        Event event = findEventById(eventRepository, eventId);
        if (userId == event.getInitiator().getId()) {
            List<Request> requests = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.PENDING);
            return requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public RequestsUpdateOutDto changeEventRequestsState(int userId, long eventId, RequestsUpdateInDto requestsUpdateInDto) {
        findUserById(userRepository, userId);
        Event event = findEventById(eventRepository, eventId);

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();
        RequestsUpdateOutDto result = new RequestsUpdateOutDto(confirmedRequests, rejectedRequests);
        if (userId == event.getInitiator().getId() && event.isRequestModeration() && event.getParticipantLimit() > 0) {
            List<Long> ids = requestsUpdateInDto.getRequestIds();
            if (requestRepository.existsByStatusAndIdIn(RequestStatus.CONFIRMED, ids)) {
                throw new ConflictRequestException("Статус можно изменить только у заявок, находящихся в состоянии ожидания!");
            }
            if (requestsUpdateInDto.getStatus() == RequestStatus.CONFIRMED) {
                //Получаем кол-во подтвержденных заявок на событие
                int approvedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
                int freeQuota = event.getParticipantLimit() - approvedRequests;
                if (freeQuota <= 0) {
                    throw new ConflictRequestException("The participant limit has been reached");
                }

                //Если все заявки из запроса вмещаются в квоту
                if (freeQuota > ids.size()) {
                    requestRepository.updateRequestStatusByIds(RequestStatus.CONFIRMED, ids);
                    confirmedRequests.addAll(requestRepository.findByIdIn(ids)
                            .stream().map(RequestMapper::toDto).collect(Collectors.toList()));
                } else {
                    //Если все заявки из запроса не вмещаются в квоту, то пробуем занять все квоты
                    int index = 0;
                    while (freeQuota > 0) {
                        int confirmed = requestRepository.updateRequestStatusByIds(RequestStatus.CONFIRMED,
                                ids.subList(index, freeQuota));
                        index += freeQuota;
                        freeQuota -= confirmed;
                    }
                    confirmedRequests.addAll(requestRepository.findByIdIn(ids.subList(0, index))
                            .stream().map(RequestMapper::toDto).collect(Collectors.toList()));
                    if (index < ids.size() - 1) {
                        requestRepository.updateRequestStatusByIds(RequestStatus.REJECTED,
                                ids.subList(index, ids.size() - 1));
                        rejectedRequests.addAll(requestRepository.findByIdIn(ids.subList(index, ids.size() - 1))
                                .stream().map(RequestMapper::toDto).collect(Collectors.toList()));

                    }

                }
            } else if (requestsUpdateInDto.getStatus() == RequestStatus.REJECTED) {
                requestRepository.updateRequestStatusByIds(RequestStatus.REJECTED,
                        ids);
                rejectedRequests.addAll(requestRepository.findByIdIn(ids)
                        .stream().map(RequestMapper::toDto).collect(Collectors.toList()));
            }
        }
        return result;
    }

    private void updateEventByEventUpdateDto(Event event, EventUpdateDto eventUpdateDto) {
        if (eventUpdateDto.getAnnotation() != null && !eventUpdateDto.getAnnotation().isEmpty()) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getCategory() != null) {
            event.setCategory(findCategoryById(categoryRepository, eventUpdateDto.getCategory()));
        }
        if (eventUpdateDto.getDescription() != null && !eventUpdateDto.getDescription().isEmpty()) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getEventDate() != null) {
            if (!eventUpdateDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дату можно поставить на не ранее чем через 2 часа");
            }
            event.setEventDate(eventUpdateDto.getEventDate());
        }
        if (eventUpdateDto.getLocation() != null) {
            event.setLatitude(eventUpdateDto.getLocation().getLat());
            event.setLongitude(eventUpdateDto.getLocation().getLon());
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getTitle() != null && !eventUpdateDto.getTitle().isEmpty()) {
            event.setTitle(eventUpdateDto.getTitle());
        }
    }

    private List<EventOutputDto> loadEventHitsAndRequestsAndMap(List<Event> events, String uri) {

        List<Object[]> eventIdAndCount = requestRepository
                .countConfirmedRequestsByEventIds(events.stream().map(Event::getId).collect(Collectors.toList()));

        HashMap<Long, Long> requestsCountByEventId = new HashMap<>();
        for (Object[] row : eventIdAndCount) {
            Long eventId = (Long) row[0];
            Long requestCount = (Long) row[1];
            requestsCountByEventId.put(eventId, requestCount);
        }

        LocalDateTime start = LocalDateTime.now();
        List<String> uris = new ArrayList<>();
        for (Event event : events) {
            if (event.getEventDate().isBefore(start)) start = event.getEventDate();
            uris.add(uri + "/" + event.getId());
        }

        List<RequestHitInfoDto> requestHits = statClient.getRequestHitInfoDto(start, LocalDateTime.now(), uris, true);
        HashMap<Long, Long> eventIdHitsCount = new HashMap<>();
        for (RequestHitInfoDto hit : requestHits) {
            eventIdHitsCount.put(Long.parseLong(hit.getUri().split("/")[3]), hit.getHits());
        }

        return events.stream()
                .map(event -> EventMapper.toDto(event, requestsCountByEventId.get(event.getId()), eventIdHitsCount.get(event.getId())))
                .collect(Collectors.toList());
    }
}
