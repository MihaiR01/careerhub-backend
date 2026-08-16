package ro.mihai.careerhub.repository;

import org.springframework.data.jpa.domain.Specification;

import ro.mihai.careerhub.entity.Job;
import ro.mihai.careerhub.enums.EmploymentType;

public final class JobSpecifications {

    private JobSpecifications() {
    }

    public static Specification<Job> hasLocation(String location) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("location")),
                        location.toLowerCase()
                );
    }

    public static Specification<Job> hasEmploymentType(
            EmploymentType employmentType) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("employmentType"),
                        employmentType
                );
    }

    public static Specification<Job> hasTechnology(
            String technology) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("technologies")
                        ),
                        "%" + technology.toLowerCase() + "%"
                );
    }
}