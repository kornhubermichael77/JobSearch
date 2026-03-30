package info.kornhuber.jobsearch.domain.repository.projection;

public interface CompanyJobCountProjection {
    Integer getCompanyId();
    Long getJobCount();
}