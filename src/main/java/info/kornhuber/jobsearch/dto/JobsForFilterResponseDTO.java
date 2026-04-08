package info.kornhuber.jobsearch.dto;

import info.kornhuber.jobsearch.enums.JobStatus;

public class JobsForFilterResponseDTO {
    public Integer id;
    public String text;
    public String source;
    public JobStatus status;
    public Integer companyId;
    public String companyName;
}