package info.kornhuber.jobsearch.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pk")
    @Getter
    @Setter
    private Integer id;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    // addresses zeigt auf Address.company
    @Getter
    @Setter
    private List<Address> addresses = new ArrayList<>();
    // Company company = ...
    //List<Address> companyAddresses = company.getAddresses();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<Job> jobs = new ArrayList<>();

    @Column(name = "name", length = 100)
    @Getter
    @Setter
    private String name;

    @Column(name = "url", length = 500)
    @Getter
    @Setter
    private String url;

    @Column(name = "url_jobs", length = 500)
    @Getter
    @Setter
    private String urlJobs;

    @Column(name = "summary")
    @Getter
    @Setter
    @Lob
    private String summary;

    @Column(name = "mail", length = 150)
    @Getter
    @Setter
    private String mail;

    @Column(name = "mail_person", length = 100)
    @Getter
    @Setter
    private String mailPerson;

    @Column(name = "tel", length = 25)
    @Getter
    @Setter
    private String tel;

    @Column(name = "tel_person", length = 100)
    @Getter
    @Setter
    private String telPerson;

    public void addJob(Job job) {
        jobs.add(job);
        job.setCompany(this);
    }

    public void removeJob(Job job) {
        jobs.remove(job);
        job.setCompany(null);
    }
}