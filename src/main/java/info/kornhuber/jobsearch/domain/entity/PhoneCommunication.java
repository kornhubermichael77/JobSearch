package info.kornhuber.jobsearch.domain.entity;

import info.kornhuber.jobsearch.enums.CommunicationDirection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "phone")
@PrimaryKeyJoinColumn(name = "comm_id_pk")
public class PhoneCommunication extends Communication {

    @Getter
    @Setter
    @Column(name = "number", length = 25)
    private String number;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private CommunicationDirection direction;
}