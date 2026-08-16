package ro.mihai.careerhub.exception;

public class DuplicateJobApplicationException extends RuntimeException {

    public DuplicateJobApplicationException(Long userId, Long jobId) {
        super(
                "User with id " + userId
                        + " has already applied to job with id " + jobId
        );
    }
}