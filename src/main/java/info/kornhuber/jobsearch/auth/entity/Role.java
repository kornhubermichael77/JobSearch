package info.kornhuber.jobsearch.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id_PK")
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
}