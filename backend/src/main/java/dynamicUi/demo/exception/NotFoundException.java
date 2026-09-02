package dynamicUi.demo.exception;

/**
 * Base type for "resource not found" domain exceptions
 * (FacilityNotFoundException, PageNotFoundException, ...).
 * GlobalExceptionHandler catches this base type and maps it to 404,
 * so each subclass only needs to exist — no extra wiring per entity.
 */
public abstract class NotFoundException extends RuntimeException {
    protected NotFoundException(String message) {
        super(message);
    }
}
