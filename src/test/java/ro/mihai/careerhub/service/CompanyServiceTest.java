package ro.mihai.careerhub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.mihai.careerhub.dto.request.CreateCompanyRequest;
import ro.mihai.careerhub.dto.response.CompanyResponse;
import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.exception.CompanyNotFoundException;
import ro.mihai.careerhub.mapper.CompanyMapper;
import ro.mihai.careerhub.repository.CompanyRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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

    @Test
    void shouldGetAllCompanies() {

        Company company1 = new Company(
                "Nexttech",
                "Cluj-Napoca",
                "https://www.nexttech.ro"
        );

        Company company2 = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        CompanyResponse response1 = new CompanyResponse(
                1L,
                "Nexttech",
                "Cluj-Napoca",
                "https://www.nexttech.ro"
        );

        CompanyResponse response2 = new CompanyResponse(
                2L,
                "Google",
                "Bucharest",
                "https://google.com"
        );

        when(companyRepository.findAll())
                .thenReturn(List.of(company1, company2));

        when(companyMapper.toResponse(company1))
                .thenReturn(response1);

        when(companyMapper.toResponse(company2))
                .thenReturn(response2);

        List<CompanyResponse> result =
                companyService.getAllCompanies();

        assertEquals(2, result.size());

        assertEquals(
                "Nexttech",
                result.get(0).getName()
        );

        assertEquals(
                "Google",
                result.get(1).getName()
        );

        assertEquals(
                "Cluj-Napoca",
                result.get(0).getCity()
        );

        assertEquals(
                "Bucharest",
                result.get(1).getCity()
        );

        verify(companyRepository).findAll();
        verify(companyMapper).toResponse(company1);
        verify(companyMapper).toResponse(company2);
    }

    @Test
    void shouldGetCompanyById() {

        Company company = new Company(
                "Nexttech",
                "Cluj-Napoca",
                "https://www.nexttech.ro"
        );

        CompanyResponse response = new CompanyResponse(
                1L,
                "Nexttech",
                "Cluj-Napoca",
                "https://www.nexttech.ro"
        );

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyMapper.toResponse(company))
                .thenReturn(response);

        CompanyResponse result =
                companyService.getCompanyById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Nexttech", result.getName());
        assertEquals("Cluj-Napoca", result.getCity());
        assertEquals(
                "https://www.nexttech.ro",
                result.getWebsite()
        );

        verify(companyRepository).findById(1L);
        verify(companyMapper).toResponse(company);
    }

    @Test
    void shouldThrowExceptionWhenCompanyDoesNotExist() {

        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> companyService.getCompanyById(999L)
        );

        verify(companyRepository).findById(999L);
        verifyNoInteractions(companyMapper);
    }
}