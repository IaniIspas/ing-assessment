package ing.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OutOfStockException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Product is out of stock";

    public OutOfStockException() {
        super(DEFAULT_MESSAGE);
    }

    public OutOfStockException(String message) {
        super(message);
    }
}
