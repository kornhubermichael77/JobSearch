package info.kornhuber.jobsearch.dto;

import java.time.LocalDateTime;

import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.enums.CommunicationDirection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateCommunicationRequest {
    /*
    Sinnvolle Zusatz-Validierung für CreateCommunicationRequest
    Je nach Typ könntest du später noch fachlich prüfen:
    bei MAIL muss subject gesetzt sein
    bei PHONE muss number gesetzt sein
    bei WEBFORM sollte url gesetzt sein
    Das geht mit einfacher if-Logik im Service oder später mit eigener Custom Validation.
     */
    @NotNull(message = "type darf nicht null sein")
    public CommunicationType type;

    @NotNull(message = "jobId darf nicht null sein")
    public Integer jobId;

    @NotNull(message = "date darf nicht null sein")
    public LocalDateTime date;

    @Size(max = 100, message = "person darf maximal 100 Zeichen haben")
    public String person;

    @Size(max = 100, message = "role darf maximal 100 Zeichen haben")
    public String role;

    public String content;
    public String sidemarks;

    @NotNull
    public CommunicationStatus status;

    // MAIL
    @Size(max = 150, message = "address darf maximal 150 Zeichen haben")
    public String address;
    @Size(max = 100, message = "subject darf maximal 100 Zeichen haben")
    public String subject;
    public String attachments;

    // PHONE
    @Size(max = 25, message = "number darf maximal 25 Zeichen haben")
    public String number;

    // PHONE, MAIL
    public CommunicationDirection direction;

    // WEBFORM
    @Size(max = 500, message = "url darf maximal 500 Zeichen haben")
    public String url;

    @Size(max = 150, message = "screenshot darf maximal 150 Zeichen haben")
    public String screenshot;

    // TALK
    @Size(max = 100, message = "location darf maximal 100 Zeichen haben")
    public String location;

    @Size(max = 100, message = "context darf maximal 100 Zeichen haben")
    public String context;

    // TRIAL, INTERVIEW
    @Size(max = 150, message = "duration darf maximal 150 Zeichen haben")
    public String duration;

    public String conclusion;
}