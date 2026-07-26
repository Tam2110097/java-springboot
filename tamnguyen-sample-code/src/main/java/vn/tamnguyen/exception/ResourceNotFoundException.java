package vn.tamnguyen.exception;

// cũng có thể để vào trong GlobalExceptionHandler để bắt như các exception khác
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
