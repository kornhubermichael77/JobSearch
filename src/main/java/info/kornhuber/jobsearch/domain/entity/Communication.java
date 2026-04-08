package info.kornhuber.jobsearch.domain.entity;

import info.kornhuber.jobsearch.enums.CommunicationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,             // als Typinformation wird der Name verwendet
        include = JsonTypeInfo.As.PROPERTY,     // die Typinformation wird als Property zugänglich
        property = "type"                       // und hat die Bezeichnung "type"
)
@JsonSubTypes({                                 // Entitäten und Namen zuordnen
        @JsonSubTypes.Type(value = MailCommunication.class, name = "MAIL"),
        @JsonSubTypes.Type(value = PhoneCommunication.class, name = "PHONE"),
        @JsonSubTypes.Type(value = TalkCommunication.class, name = "TALK"),
        @JsonSubTypes.Type(value = TrialCommunication.class, name = "TRIAL"),
        @JsonSubTypes.Type(value = InterviewCommunication.class, name = "INTERVIEW"),
        @JsonSubTypes.Type(value = WebformCommunication.class, name = "WEBFORM")
})

@Entity
@Table(name = "communication")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Communication {

    /* Zwingt alle Unterklassen, eine solche Methode zu implementieren:

        Zweck: Wandelt eine Datenbank-Entity (Communication) in ein DTO (TimelineItemDTO) um.
        Typische Aufgaben:
            Nur bestimmte Felder exponieren (z.B. keine internen IDs).
            Felder umbenennen (z.B. communicationDate → timestamp).
            Komplexe Objekte vereinfachen (z.B. job.getName() → jobName).
    */


    // allgemeine Variablen und deren Herkunft, Getter, Rolle, ...
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pk")
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "job_id_fk")
    private Job job;

    @Getter
    @Setter
    @Column(name = "date")
    private LocalDateTime date;

    @Getter
    @Setter
    @Column(name = "person", length = 100)
    private String person;

    @Getter
    @Setter
    @Column(name = "role", length = 100)
    private String role;

    @Getter
    @Setter
    @Lob
    @Column(name = "content")
    private String content;

    @Getter
    @Setter
    @Lob
    @Column(name = "sidemarks")
    private String sidemarks;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private CommunicationStatus status;

}