package ing.assessment.exception;

import ing.assessment.dto.error.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {
    private final HttpServletRequest httpServletRequest;

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        var path = httpServletRequest.getRequestURI();
        log.error("Error occurred: {} [Path: {}]", message, path);

        var errorDto = new ErrorDto(status, message, path);

        return new ResponseEntity<>(errorDto, errorDto.getStatus());
    }

    @ExceptionHandler({
        ProductNotFoundException.class,
        OutOfStockException.class,
        InvalidOrderException.class
    })
    protected ResponseEntity<Object> handleException(Exception ex) {
        HttpStatus status;
        var message = ex.getMessage();

        switch (ex.getClass().getSimpleName()) {
            case "ProductNotFoundException" ->
                status = HttpStatus.NOT_FOUND;
            case "OutOfStockException", "InvalidOrderException" ->
                status = HttpStatus.BAD_REQUEST;
            default ->
                status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return buildErrorResponse(status, message);
    }
}
