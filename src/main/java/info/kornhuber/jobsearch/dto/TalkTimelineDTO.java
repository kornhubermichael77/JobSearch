package info.kornhuber.jobsearch.dto;

public class TalkTimelineDTO extends TimelineItemDTO{
    // Felder die nur dieses TimelineItem hat.
    // Die anderen Felder werden von der Basisklasse geerbt.
    public String location;
    public String context;
}
