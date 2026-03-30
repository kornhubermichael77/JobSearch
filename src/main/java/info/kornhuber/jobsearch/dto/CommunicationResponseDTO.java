package info.kornhuber.jobsearch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.enums.CommunicationDirection;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommunicationResponseDTO {

    public Integer id;
    public CommunicationType type;

    public Integer jobId;
    public LocalDateTime date;
    public String person;
    public String role;
    public String content;
    public String sidemarks;
    public CommunicationStatus status;

    // MAIL
    public String address;
    public String subject;
    public String attachments;

    // PHONE
    public String number;

    // PHONE, MAIL
    public CommunicationDirection direction;

    // TALK
    public String location;
    public String context;

    // TRIAL / INTERVIEW
    public String duration;
    public String conclusion;

    // WEBFORM
    public String url;
    public String screenshot;
}