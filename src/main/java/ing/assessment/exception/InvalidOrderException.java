package ing.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOrderException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid order data provided";

    public InvalidOrderException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidOrderException(String message) {
        super(message);
    }
}
