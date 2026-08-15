package ro.mihai.careerhub.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateJobApplicationRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long jobId;

    public CreateJobApplicationRequest() {
    }

    public CreateJobApplicationRequest(
            Long userId,
            Long jobId) {

        this.userId = userId;
        this.jobId = jobId;
    }
}