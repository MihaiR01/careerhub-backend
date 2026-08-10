package ro.mihai.careerhub.exception;

public class CompanyNotFoundException extends RuntimeException {

    public CompanyNotFoundException(Long id) {
        super("Company with id " + id + " not found");
    }
}