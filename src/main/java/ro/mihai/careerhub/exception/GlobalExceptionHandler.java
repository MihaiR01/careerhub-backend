package ro.mihai.careerhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleJobNotFoundException() {
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleCompanyNotFoundException() {
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(
            UserNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(JobApplicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleJobApplicationNotFound() {
    }

    @ExceptionHandler(InvalidApplicationStatusTransitionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleInvalidApplicationStatusTransition() {
    }

    @ExceptionHandler(DuplicateJobApplicationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleDuplicateJobApplication() {
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleAccessDenied() {
    }
}