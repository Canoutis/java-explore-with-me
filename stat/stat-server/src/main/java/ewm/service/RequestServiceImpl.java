package ewm.service;

import ewm.mapper.RequestMapper;
import ewm.model.Request;
import ewm.model.RequestHitDto;
import ewm.model.RequestHitInfoDto;
import ewm.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional
    public RequestHitDto create(RequestHitDto requestHitDto) {
        Request request = RequestMapper.dtoToEntity(requestHitDto);
        return RequestMapper.entityToDto(requestRepository.save(request));
    }

    @Override
    public List<RequestHitInfoDto> getStatistic(LocalDateTime start, LocalDateTime end,
                                                List<String> uris, boolean unique) {
        List<RequestHitInfoDto> hits;
        if (uris == null || uris.isEmpty()) {
            if (unique) hits = requestRepository.findByDateUnique(start, end);
            else hits = requestRepository.findByDate(start, end);
        } else {
            if (unique) hits = requestRepository.findByDateAndUriUnique(start, end, uris);
            else hits = requestRepository.findByDateAndUri(start, end, uris);
        }
        return hits.stream()
                .sorted(Comparator.comparingLong(RequestHitInfoDto::getHits).reversed())
                .collect(Collectors.toList());
    }
}
