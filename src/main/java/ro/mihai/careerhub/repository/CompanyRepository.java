package ro.mihai.careerhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.mihai.careerhub.entity.Company;

public interface CompanyRepository
        extends JpaRepository<Company, Long> {

}
