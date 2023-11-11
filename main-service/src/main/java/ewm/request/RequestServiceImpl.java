package ewm.request;

import ewm.event.Event;
import ewm.event.EventRepository;
import ewm.event.EventState;
import ewm.exception.ConflictRequestException;
import ewm.exception.ObjectNotFoundException;
import ewm.request.dto.RequestDto;
import ewm.user.User;
import ewm.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ewm.utils.Helper.findEventById;
import static ewm.utils.Helper.findUserById;

@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository,
                              EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<RequestDto> findUserRequests(int userId) {
        findUserById(userRepository, userId);
        List<Request> requests = requestRepository.findByRequesterId(userId);
        return requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto create(int userId, long eventId) {
        User requester = findUserById(userRepository, userId);
        Event event = findEventById(eventRepository, eventId);
        if (requestRepository.countByRequesterIdAndEventId(userId, eventId) > 0 ||
                userId == event.getInitiator().getId() || event.getState() != EventState.PUBLISHED ||
                (event.getParticipantLimit() > 0 &&
                        requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit())) {
            throw new ConflictRequestException("Оставить запрос на участие в событии недоступно.");
        } else {
            Request request = Request.builder()
                    .requester(requester)
                    .event(event)
                    .status(event.isRequestModeration() && event.getParticipantLimit() != 0 ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                    .build();
            return RequestMapper.toDto(requestRepository.save(request));
        }
    }

    @Override
    @Transactional
    public RequestDto cancelRequestByOwner(int userId, long requestId) {
        findUserById(userRepository, userId);
        Optional<Request> requestOptional = requestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Запрос на участие с id=%d не найден!", requestId));
        } else {
            Request request = requestOptional.get();
            request.setStatus(RequestStatus.CANCELED);
            return RequestMapper.toDto(requestRepository.save(request));
        }
    }
}
