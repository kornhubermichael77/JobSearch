package info.kornhuber.jobsearch.dto;

import info.kornhuber.jobsearch.enums.CommunicationStatus;
import lombok.Getter;

public class JobSummaryDTO {
    // Felder, die dieses Objekt beschreiben
    @Getter
    public Integer id;

    @Getter
    public String companyName;

    @Getter
    public CommunicationStatus status;

}