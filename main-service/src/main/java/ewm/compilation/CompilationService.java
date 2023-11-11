package ewm.compilation;

import ewm.compilation.dto.CompilationRequestDto;
import ewm.compilation.dto.CompilationResultDto;
import ewm.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {
    List<CompilationResultDto> getCompilations(Boolean pinned, int from, int size);

    CompilationResultDto getCompilationById(long compId);

    CompilationResultDto create(CompilationRequestDto compilationRequestDto);

    void deleteById(long compId);

    CompilationResultDto updateById(long compId, CompilationUpdateDto compilationUpdateDto);
}
