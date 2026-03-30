package info.kornhuber.jobsearch.dto;

public class WebformTimelineDTO extends TimelineItemDTO {
    // Felder die nur dieses TimelineItem hat.
    // Die anderen Felder werden von der Basisklasse geerbt.
    public String url;
    public String screenshot;
}
