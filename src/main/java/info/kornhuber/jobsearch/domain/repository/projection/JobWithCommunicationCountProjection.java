package info.kornhuber.jobsearch.domain.repository.projection;

import java.time.LocalDateTime;
import info.kornhuber.jobsearch.enums.JobStatus;

public interface JobWithCommunicationCountProjection {
    Integer getId();
    Integer getCompanyId();
    String getCompanyName();

    Integer getAddressId();
    String getCity();
    String getStreet();
    String getNumber();
    String getPostcode();
    String getCountry();
    Boolean getHeadquarter();
    Double getDistance();
    LocalDateTime getTraveltime();

    LocalDateTime getFound();
    String getSource();
    String getUrl();
    String getText();
    JobStatus getStatus();
    String getMail();
    String getMailPerson();
    String getTel();
    String getTelPerson();
    String getTeilzeit();
    String getGleitzeit();
    String getHomeoffice();
    String getFeatures();
    Long getCommunicationCount();
}
