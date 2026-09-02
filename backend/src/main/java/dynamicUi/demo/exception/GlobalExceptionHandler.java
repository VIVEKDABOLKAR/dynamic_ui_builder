package dynamicUi.demo.exception;

import dynamicUi.demo.controller.FacilityAdminController;
import dynamicUi.demo.controller.UIPageController;
import dynamicUi.demo.controller.WorkflowStepAdminController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Basic global exception handler.
 *
 * Scoped (via assignableTypes) to a first batch of 3 controllers —
 * UIPageController, FacilityAdminController, WorkflowStepAdminController —
 * so we can validate the response shape before wiring it up app-wide.
 * To extend it to more controllers later, just add them to assignableTypes.
 *
 * Covers the exception types these modules' services actually throw today:
 * specific NotFoundException / AlreadyExistsException subclasses (e.g.
 * FacilityNotFoundException, PageAlreadyExistsException), plus the
 * pre-existing ResponseStatusException (still used for simple field
 * validation), IllegalArgumentException / IllegalStateException, and a
 * generic fallback for anything unexpected.
 */
@RestControllerAdvice(assignableTypes = {
        UIPageController.class,
        FacilityAdminController.class,
        WorkflowStepAdminController.class
})
public class GlobalExceptionHandler {

    // Specific "not found" exceptions, e.g. FacilityNotFoundException,
    // PageNotFoundException, RouteNotFoundException, WorkflowStepNotFoundException.
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Specific "already exists" / conflict exceptions, e.g.
    // FacilityAlreadyExistsException, PageAlreadyExistsException,
    // RouteAlreadyExistsException, WorkflowStepAlreadyExistsException.
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(AlreadyExistsException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // Thrown across the codebase with an explicit HttpStatus + message,
    // e.g. ResponseStatusException(HttpStatus.CONFLICT, "Facility ID is required.")
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getReason()
        );
        return ResponseEntity.status(status).body(body);
    }

    // Bad input caught by manual checks in the service layer.
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    // Plain "throw new RuntimeException(...)" business errors, e.g.
    // "Page code already exists" in UIPageServiceImp — treated as a
    // client-facing 400 rather than a raw 500 + stack trace.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    // Anything not covered above — never leak a raw stack trace to the client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Something went wrong. Please try again."
        );
        return ResponseEntity.internalServerError().body(body);
    }
}
