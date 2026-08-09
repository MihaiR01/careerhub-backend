package ro.mihai.careerhub.mapper;

import org.junit.jupiter.api.Test;
import ro.mihai.careerhub.dto.response.CompanyResponse;
import ro.mihai.careerhub.entity.Company;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompanyMapperTest {

    private final CompanyMapper companyMapper = new CompanyMapper();

    @Test
    void shouldMapCompanyToResponse() {

        Company company = new Company(
                "Nexttech",
                "Cluj-Napoca",
                "https://nexttech.talentlyft.com/"
        );

        CompanyResponse response = companyMapper.toResponse(company);

        assertEquals("Nexttech", response.getName());
        assertEquals("Cluj-Napoca", response.getCity());
        assertEquals("https://nexttech.talentlyft.com/", response.getWebsite());
    }
}