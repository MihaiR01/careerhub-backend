package ro.mihai.careerhub.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ro.mihai.careerhub.enums.EmploymentType;

@Entity
@Getter
@Setter
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String technologies;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdate;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    protected Job() {
    }

    public Job(
            String title,
            String technologies,
            String location,
            EmploymentType employmentType,
            Company company) {

        this.title = title;
        this.technologies = technologies;
        this.location = location;
        this.employmentType = employmentType;
        this.company = company;
    }

    @PrePersist
    protected void onCreate() {
        createdate = LocalDateTime.now();
    }
    
}