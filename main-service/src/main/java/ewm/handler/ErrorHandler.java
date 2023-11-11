package ewm.handler;

import ewm.exception.BadRequestException;
import ewm.exception.ConflictRequestException;
import ewm.exception.ObjectAccessException;
import ewm.exception.ObjectNotFoundException;
import ewm.exception.ObjectSaveException;
import ewm.exception.ObjectUpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundException(final ObjectNotFoundException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getStackTrace(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleObjectSaveException(final ObjectSaveException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getStackTrace(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleObjectUpdateException(final ObjectUpdateException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getStackTrace(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleObjectAccessException(final ObjectAccessException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getStackTrace(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getStackTrace(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictRequestException(final ConflictRequestException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getStackTrace(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getStackTrace(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ErrorResponse(ex.getStackTrace(), "Unknown " + ex.getName() + ": " + ex.getValue());
    }
}
