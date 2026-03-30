package info.kornhuber.jobsearch.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "webform")
@PrimaryKeyJoinColumn(name = "comm_id_pk")
public class WebformCommunication extends Communication {

    @Getter
    @Setter
    @Column(name = "url", length = 500)
    private String url;

    @Getter
    @Setter
    @Column(name = "screenshot", length = 150)
    private String screenshot;
}
