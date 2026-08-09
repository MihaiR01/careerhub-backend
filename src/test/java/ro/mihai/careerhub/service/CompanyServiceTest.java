package ro.mihai.careerhub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mihai.careerhub.dto.request.CreateCompanyRequest;
import ro.mihai.careerhub.dto.response.CompanyResponse;
import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.mapper.CompanyMapper;
import ro.mihai.careerhub.repository.CompanyRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void shouldCreateCompany() {

        CreateCompanyRequest request = new CreateCompanyRequest(
                "Nexttech",
                "Cluj-Napoca",
                "https://nexttech.talentlyft.com/"
        );

        Company company = new Company(
                "Nexttech",
                "Cluj-Napoca",
                "https://nexttech.talentlyft.com/"
        );

        CompanyResponse response = new CompanyResponse(
                1L,
                "Nexttech",
                "Cluj-Napoca",
                "https://nexttech.talentlyft.com/"
        );

        when(companyRepository.save(any(Company.class)))
                .thenReturn(company);

        when(companyMapper.toResponse(company))
                .thenReturn(response);

        CompanyResponse result =
                companyService.createCompany(request);

        assertEquals(1L, result.getId());
        assertEquals("Nexttech", result.getName());
        assertEquals("Cluj-Napoca", result.getCity());
        assertEquals(
                "https://nexttech.talentlyft.com/",
                result.getWebsite()
        );

        verify(companyRepository).save(any(Company.class));
        verify(companyMapper).toResponse(company);
    }
}