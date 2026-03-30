package info.kornhuber.jobsearch.domain.entity;

import info.kornhuber.jobsearch.enums.CommunicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pk")
    @Getter
    @Setter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "c_id_fk")
    @Getter
    @Setter
    private Company company;

    // Mehrere Jobs können auf die gleiche Adresse zeigen
    @ManyToOne
    @JoinColumn(name = "a_id_fk")
    @Getter
    @Setter
    private Address address;

    @Column(name = "found")
    @Getter
    @Setter
    private LocalDateTime found;

    @Column(name = "source", length = 150)
    @Getter
    @Setter
    private String source;

    @Column(name = "url", length = 500)
    @Getter
    @Setter
    private String url;

    @Column(name = "text")
    @Getter
    @Setter
    @Lob
    private String text;

    @Column(name = "status", length = 150)
    @Getter
    @Setter
    private CommunicationStatus status;

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

    @Column(name = "teilzeit", length = 100)
    @Getter
    @Setter
    private String teilzeit;

    @Column(name = "gleitzeit", length = 100)
    @Getter
    @Setter
    private String gleitzeit;

    @Column(name = "homeoffice", length = 100)
    @Getter
    @Setter
    private String homeoffice;

    @Column(name = "features")
    @Getter
    @Setter
    @Lob
    private String features;

}