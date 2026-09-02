package dynamicUi.demo.exception;

/**
 * Base type for "resource already exists" / conflict domain exceptions
 * (FacilityAlreadyExistsException, PageAlreadyExistsException, ...).
 * GlobalExceptionHandler catches this base type and maps it to 409,
 * so each subclass only needs to exist — no extra wiring per entity.
 */
public abstract class AlreadyExistsException extends RuntimeException {
    protected AlreadyExistsException(String message) {
        super(message);
    }
}
