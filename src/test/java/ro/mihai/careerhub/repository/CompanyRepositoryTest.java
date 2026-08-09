package ro.mihai.careerhub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import ro.mihai.careerhub.entity.Company;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldSaveAndFindCompany() {

        Company company = new Company(
                "Nexttech",
                "Cluj-Napoca",
                "https://www.nexttech.ro"
        );

        Company savedCompany = companyRepository.save(company);

        assertNotNull(savedCompany.getId());

        assertEquals(
                "Nexttech",
                savedCompany.getName()
        );

        assertEquals(
                "Cluj-Napoca",
                savedCompany.getCity()
        );

        assertEquals(
                "https://www.nexttech.ro",
                savedCompany.getWebsite()
        );
    }
}