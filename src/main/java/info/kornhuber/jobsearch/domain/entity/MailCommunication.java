package info.kornhuber.jobsearch.domain.entity;

import info.kornhuber.jobsearch.enums.CommunicationDirection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mail")
@PrimaryKeyJoinColumn(name = "comm_id_pk")
public class MailCommunication extends Communication {

    @Getter
    @Setter
    @Column(name = "address", length = 150)
    private String address;

    @Getter
    @Setter
    @Column(name = "subject", length = 100)
    private String subject;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private CommunicationDirection direction;

    @Getter
    @Setter
    @Lob
    @Column(name = "attachments")
    private String attachments;
}