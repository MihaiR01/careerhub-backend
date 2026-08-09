package ro.mihai.careerhub.mapper;

import org.springframework.stereotype.Component;
import ro.mihai.careerhub.dto.response.CompanyResponse;
import ro.mihai.careerhub.entity.Company;

@Component
public class CompanyMapper {

    public CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getCity(),
                company.getWebsite()
        );
    }
}