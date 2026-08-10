package ro.mihai.careerhub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ro.mihai.careerhub.dto.request.CreateCompanyRequest;
import ro.mihai.careerhub.dto.response.CompanyResponse;
import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.exception.CompanyNotFoundException;
import ro.mihai.careerhub.mapper.CompanyMapper;
import ro.mihai.careerhub.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyService(
            CompanyRepository companyRepository,
            CompanyMapper companyMapper) {

        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public CompanyResponse createCompany(CreateCompanyRequest request) {

        Company company = new Company(
                request.getName(),
                request.getCity(),
                request.getWebsite()
        );

        Company savedCompany = companyRepository.save(company);

        return companyMapper.toResponse(savedCompany);
    }

    public List<CompanyResponse> getAllCompanies() {

        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toResponse)
                .toList();
    }

    public CompanyResponse getCompanyById(Long id) {

        Company company = companyRepository
                .findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));

        return companyMapper.toResponse(company);
    }
}
