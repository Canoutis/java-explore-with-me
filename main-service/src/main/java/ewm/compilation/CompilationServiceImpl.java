package ewm.compilation;

import ewm.compilation.dto.CompilationRequestDto;
import ewm.compilation.dto.CompilationResultDto;
import ewm.compilation.dto.CompilationUpdateDto;
import ewm.event.Event;
import ewm.event.EventRepository;
import ewm.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }


    @Override
    public List<CompilationResultDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageable)
                    .stream().map(CompilationMapper::toDto).collect(Collectors.toList());
        } else {
            return compilationRepository.findAll(pageable)
                    .stream().map(CompilationMapper::toDto).collect(Collectors.toList());
        }
    }

    @Override
    public CompilationResultDto getCompilationById(long compId) {
        return CompilationMapper.toDto(findCompilationById(compId));
    }

    @Override
    @Transactional
    public CompilationResultDto create(CompilationRequestDto compilationRequestDto) {
        List<Event> events = eventRepository.findAllById(compilationRequestDto.getEvents());
        Compilation compilation = CompilationMapper.toEntity(compilationRequestDto, events);
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteById(long compId) {
        findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationResultDto updateById(long compId, CompilationUpdateDto compilationUpdateDto) {
        Compilation compilation = findCompilationById(compId);
        if (compilationUpdateDto.getPinned() != null) {
            compilation.setPinned(compilationUpdateDto.getPinned());
        }
        if (compilationUpdateDto.getTitle() != null) {
            compilation.setTitle(compilationUpdateDto.getTitle());
        }
        if (compilationUpdateDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(compilationUpdateDto.getEvents()));
        }
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    private Compilation findCompilationById(long compId) {
        Optional<Compilation> compilationOptional = compilationRepository.findById(compId);
        if (compilationOptional.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Подборка с id=%d не найдена!", compId));
        }
        return compilationOptional.get();
    }
}
