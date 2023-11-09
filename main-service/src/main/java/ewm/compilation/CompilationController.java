package ewm.compilation;

import ewm.compilation.dto.CompilationRequestDto;
import ewm.compilation.dto.CompilationResultDto;
import ewm.compilation.dto.CompilationUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CompilationController {

    private final CompilationService compilationService;

    @Autowired
    public CompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping(value = "/compilations")
    public ResponseEntity<List<CompilationResultDto>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                      @RequestParam(defaultValue = "0", required = false) Integer from,
                                                                      @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(compilationService.getCompilations(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping(value = "/compilations/{compId}")
    public ResponseEntity<CompilationResultDto> getCompilationById(@PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.getCompilationById(compId), HttpStatus.OK);
    }

    @PostMapping(value = "/admin/compilations")
    public ResponseEntity<CompilationResultDto> create(@Valid @RequestBody CompilationRequestDto compilationRequestDto) {
        return new ResponseEntity<>(compilationService.create(compilationRequestDto), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/admin/compilations/{compId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long compId) {
        compilationService.deleteById(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/admin/compilations/{compId}")
    public ResponseEntity<CompilationResultDto> updateById(@PathVariable Long compId, @Valid @RequestBody CompilationUpdateDto compilationUpdateDto) {
        return new ResponseEntity<>(compilationService.updateById(compId, compilationUpdateDto), HttpStatus.OK);
    }

}
