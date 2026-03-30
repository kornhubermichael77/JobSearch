package info.kornhuber.jobsearch.dto;

public class InterviewTimelineDTO extends TimelineItemDTO{
    // Felder die nur dieses TimelineItem hat.
    // Die anderen Felder werden von der Basisklasse geerbt.
    public String duration;
    public String conclusion;
}
