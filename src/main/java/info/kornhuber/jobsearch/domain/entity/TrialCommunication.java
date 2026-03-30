package info.kornhuber.jobsearch.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trial")
@PrimaryKeyJoinColumn(name = "comm_id_pk")
public class TrialCommunication extends Communication {

    @Getter
    @Setter
    @Column(name = "duration", length = 150)
    private String duration;

    @Getter
    @Setter
    @Lob
    @Column(name = "conclusion")
    private String conclusion;
}