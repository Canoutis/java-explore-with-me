package ewm.controller;

import ewm.model.RequestHitDto;
import ewm.model.RequestHitInfoDto;
import ewm.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@Validated
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/hit")
    public ResponseEntity<RequestHitDto> create(@Valid @RequestBody RequestHitDto requestHitDto) {
        return new ResponseEntity<>(requestService.create(requestHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<RequestHitInfoDto>> findHits(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                            @RequestParam(required = false) List<String> uris,
                                                            @RequestParam(required = false) boolean unique) {
        return new ResponseEntity<>(requestService.getStatistic(start, end, uris, unique), HttpStatus.OK);
    }
}
