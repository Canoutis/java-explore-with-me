package ewm.handler;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    List<String> errors;
    String message;
    String reason;
    String status;
    String timestamp;

    public ErrorResponse(StackTraceElement[] trace, String message) {
        this.errors = new ArrayList<>();
        for (StackTraceElement traceElement : trace) errors.add(traceElement.toString());
        this.message = message;
    }

    public ErrorResponse(String message) {
        this.message = message;
    }
}
