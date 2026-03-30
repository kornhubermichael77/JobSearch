package info.kornhuber.jobsearch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL) // verhindern, dass null-Felder im JSON auftauchen
public abstract class TimelineItemDTO {
    // Felder die alle TimelineItems haben.
    // Wird ergänzt durch die konkreten Felder der konkreten TimelineItems
    public Integer id;
    public Integer jobId;
    public CommunicationType type;
    public LocalDateTime date;
    public String person;
    public String role;
    public String content;
    public String sidemarks;
    public CommunicationStatus status; // ENUM

}
