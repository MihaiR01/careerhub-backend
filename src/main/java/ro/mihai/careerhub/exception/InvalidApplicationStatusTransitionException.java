package ro.mihai.careerhub.exception;

import ro.mihai.careerhub.enums.ApplicationStatus;

public class InvalidApplicationStatusTransitionException
        extends RuntimeException {

    public InvalidApplicationStatusTransitionException(
            ApplicationStatus currentStatus,
            ApplicationStatus nextStatus) {

        super(
                "Invalid application status transition from "
                        + currentStatus
                        + " to "
                        + nextStatus
        );
    }
}