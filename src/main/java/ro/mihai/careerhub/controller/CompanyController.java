package ro.mihai.careerhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ro.mihai.careerhub.dto.request.CreateCompanyRequest;
import ro.mihai.careerhub.dto.response.CompanyResponse;
import ro.mihai.careerhub.service.CompanyService;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CreateCompanyRequest request) {

        CompanyResponse response = companyService.createCompany(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {

        return ResponseEntity.ok(
                companyService.getAllCompanies()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(
            @PathVariable Long id) {

        CompanyResponse response = companyService.getCompanyById(id);

        return ResponseEntity.ok(response);
    }
}