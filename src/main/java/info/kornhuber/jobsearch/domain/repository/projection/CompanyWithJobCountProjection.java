package info.kornhuber.jobsearch.domain.repository.projection;

public interface CompanyWithJobCountProjection {
    Integer getId();
    String getName();
    String getMail();
    String getMailPerson();
    String getTel();
    String getTelPerson();
    String getSummary();
    String getUrl();
    String getUrlJobs();
    Long getJobCount();
}