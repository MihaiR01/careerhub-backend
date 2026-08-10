package ro.mihai.careerhub.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import ro.mihai.careerhub.enums.EmploymentType;

@Getter
public class JobResponse {

    private Long id;
    private String title;
    private String technologies;
    private String location;
    private EmploymentType employmentType;
    private LocalDateTime createdate;
    private Long companyId;

    public JobResponse(
            Long id,
            String title,
            String technologies,
            String location,
            EmploymentType employmentType,
            LocalDateTime createdate,
            Long companyId) {

        this.id = id;
        this.title = title;
        this.technologies = technologies;
        this.location = location;
        this.employmentType = employmentType;
        this.createdate = createdate;
        this.companyId = companyId;
    }
}