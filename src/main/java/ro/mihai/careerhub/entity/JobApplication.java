package ro.mihai.careerhub.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import ro.mihai.careerhub.enums.ApplicationStatus;

@Entity
@Getter
@Setter
@Table(
        name = "job_applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_application_user_job",
                        columnNames = {
                                "user_id",
                                "job_id"
                        }
                )
        }
)
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdate = LocalDateTime.now();

    public JobApplication() {
    }

    public JobApplication(
            User user,
            Job job,
            ApplicationStatus status) {

        this.user = user;
        this.job = job;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        createdate = LocalDateTime.now();
    }
}