package info.kornhuber.jobsearch.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "address")
public class Address {

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

    // Eine Adresse kann bei mehreren Jobs vorkommen
    @OneToMany(mappedBy = "address")
    @Getter
    @Setter
    private List<Job> jobs;

    @Column(name = "headquarter")
    @Getter
    @Setter
    private Boolean headquarter;

    @Column(name = "street", length = 100)
    @Getter
    @Setter
    private String street;

    @Column(name = "number", length = 30)
    @Getter
    @Setter
    private String number;

    @Column(name = "postcode", length = 10)
    @Getter
    @Setter
    private String postcode;

    @Column(name = "city", length = 60)
    @Getter
    @Setter
    private String city;

    @Column(name = "country", length = 50)
    @Getter
    @Setter
    private String country;

    @Column(name = "distance")
    @Getter
    @Setter
    private Double distance;

    @Column(name = "traveltime")
    @Getter
    @Setter
    private LocalDateTime traveltime;
}
