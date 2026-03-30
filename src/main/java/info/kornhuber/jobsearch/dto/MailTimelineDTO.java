package info.kornhuber.jobsearch.dto;

import info.kornhuber.jobsearch.enums.CommunicationDirection;

public class MailTimelineDTO extends TimelineItemDTO {
    // Felder die nur dieses TimelineItem hat.
    // Die anderen Felder werden von der Basisklasse geerbt.
    public String address;
    public String subject;
    public String attachments;
    public CommunicationDirection direction;
}
