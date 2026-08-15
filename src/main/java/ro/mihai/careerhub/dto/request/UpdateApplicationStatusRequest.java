package ro.mihai.careerhub.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import ro.mihai.careerhub.enums.ApplicationStatus;

@Getter
@Setter
public class UpdateApplicationStatusRequest {

    @NotNull
    private ApplicationStatus status;

    public UpdateApplicationStatusRequest() {
    }

    public UpdateApplicationStatusRequest(
            ApplicationStatus status) {

        this.status = status;
    }
}