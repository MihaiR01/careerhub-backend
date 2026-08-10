package ro.mihai.careerhub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ro.mihai.careerhub.enums.EmploymentType;

@Setter
@Getter
public class CreateJobRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String technologies;

    @NotBlank
    private String location;

    @NotNull
    private EmploymentType employmentType;

    @NotNull
    private Long companyId;

    public CreateJobRequest() {
    }

    public CreateJobRequest(
            String title,
            String technologies,
            String location,
            EmploymentType employmentType,
            Long companyId) {

        this.title = title;
        this.technologies = technologies;
        this.location = location;
        this.employmentType = employmentType;
        this.companyId = companyId;
    }
}