package ewm.exception;

public class ConflictRequestException extends RuntimeException {
    public ConflictRequestException(final String message) {
        super(message);
    }
}
