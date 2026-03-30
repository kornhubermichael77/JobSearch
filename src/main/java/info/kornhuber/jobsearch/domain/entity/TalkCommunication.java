package info.kornhuber.jobsearch.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "talk")
@PrimaryKeyJoinColumn(name = "comm_id_pk")
public class TalkCommunication extends Communication {

    @Getter
    @Setter
    @Column(name = "location", length = 100)
    private String location;

    @Getter
    @Setter
    @Column(name = "context", length = 100)
    private String context;
}