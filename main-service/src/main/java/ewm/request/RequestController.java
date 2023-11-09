package ewm.request;

import ewm.request.dto.RequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> findUserRequests(@PathVariable Integer userId) {
        return new ResponseEntity<>(requestService.findUserRequests(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RequestDto> create(@PathVariable Integer userId, @RequestParam Long eventId) {
        return new ResponseEntity<>(requestService.create(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{requestId}/cancel")
    public ResponseEntity<RequestDto> cancelRequestByOwner(@PathVariable Integer userId, @PathVariable Long requestId) {
        return new ResponseEntity<>(requestService.cancelRequestByOwner(userId, requestId), HttpStatus.OK);
    }

}
