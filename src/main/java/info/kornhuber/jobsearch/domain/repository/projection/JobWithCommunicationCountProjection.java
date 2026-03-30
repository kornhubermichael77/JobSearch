package info.kornhuber.jobsearch.domain.repository.projection;

import java.time.LocalDateTime;
import info.kornhuber.jobsearch.enums.CommunicationStatus;

public interface JobWithCommunicationCountProjection {
    Integer getId();
    Integer getCompanyId();
    String getCompanyName();
    Integer getAddressId();
    String getCity();
    String getStreet();
    String getNumber();
    LocalDateTime getFound();
    String getSource();
    String getUrl();
    String getText();
    CommunicationStatus getStatus();
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