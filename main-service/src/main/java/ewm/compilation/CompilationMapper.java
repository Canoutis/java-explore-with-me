package ewm.compilation;

import ewm.compilation.dto.CompilationRequestDto;
import ewm.compilation.dto.CompilationResultDto;
import ewm.event.Event;
import ewm.event.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationResultDto toDto(Compilation compilation) {
        return CompilationResultDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream().map(EventMapper::toDto).collect(Collectors.toList()))
                .pinned(compilation.isPinned())
                .build();
    }

    public static Compilation toEntity(CompilationRequestDto compilationDto, List<Event> events) {
        return Compilation.builder()
                .id(compilationDto.getId())
                .title(compilationDto.getTitle())
                .events(events)
                .pinned(compilationDto.getPinned())
                .build();
    }

}
