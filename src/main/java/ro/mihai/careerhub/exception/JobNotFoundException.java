package ro.mihai.careerhub.exception;

public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(Long id) {
        super("Job with id " + id + " not found");
    }
}