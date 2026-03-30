package info.kornhuber.jobsearch.dto;

import info.kornhuber.jobsearch.enums.CommunicationDirection;

public class PhoneTimelineDTO extends TimelineItemDTO{
    // Felder die nur dieses TimelineItem hat.
    // Die anderen Felder werden von der Basisklasse geerbt.
    public String number;
    public CommunicationDirection direction; // ENUM
}
