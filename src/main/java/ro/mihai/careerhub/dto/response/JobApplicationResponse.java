package ro.mihai.careerhub.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;

import ro.mihai.careerhub.enums.ApplicationStatus;

@Getter
public class JobApplicationResponse {

    private Long id;
    private Long userId;
    private Long jobId;
    private ApplicationStatus status;
    private LocalDateTime createdate;

    public JobApplicationResponse(
            Long id,
            Long userId,
            Long jobId,
            ApplicationStatus status,
            LocalDateTime createdate) {

        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.status = status;
        this.createdate = createdate;
    }
}