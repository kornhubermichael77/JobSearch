package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.dto.TimelineItemDTO;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.domain.entity.InterviewCommunication;
import info.kornhuber.jobsearch.domain.entity.MailCommunication;
import info.kornhuber.jobsearch.domain.entity.PhoneCommunication;
import info.kornhuber.jobsearch.domain.entity.TalkCommunication;
import info.kornhuber.jobsearch.domain.entity.TrialCommunication;
import info.kornhuber.jobsearch.domain.entity.WebformCommunication;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.mapper.TimelineMapper;
import info.kornhuber.jobsearch.domain.repository.CommunicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimelineService {

    // Objekt für die Datenbankabfrage. Als Unterart hat es auch die spezifischen (Abfrage)Methoden von diesem.
    private final CommunicationRepository repository;
    private final TimelineMapper timelineMapper;

    // DI mit erstellter Interface-Implementierung (vgl. oberhalb!)
    public TimelineService(CommunicationRepository repository, TimelineMapper timelineMapper) {
        this.repository = repository;
        this.timelineMapper = timelineMapper;
    }

    // Die timeline-Methode soll ein Page-Objekt zurückliefern. Bekommt jobID mit, sowie Auftragsdetails für das Page-Objekt.
    public Page<TimelineItemDTO> timeline(
            Integer job,      // jobId
            String type,
            String person,
            CommunicationStatus status,
            LocalDate from,
            Pageable pageable   //z.B. wie sortiert, wieviele pro Seite und welche Seite
    ) {
        if (type != null && !type.isBlank()) {
            mapTypeToClass(type.trim());
        }
        // alle Arten von Filterkriterien:
        Specification<Communication> spec = buildSpec(job, type, person, status, from);
        return repository.findAll(spec, pageable) // gibt Page-Objekt zurück
                .map(timelineMapper::toDto); // und wandelt es mit der Mapper-Klasse in DTO-Objekte um
    }
    // Was ist Specification genau? Specification kommt aus Spring Data JPA:
    //org.springframework.data.jpa.domain.Specification
    //
    //Sie ist dafür gedacht, dynamische Datenbankabfragen zu bauen.
    //Typisch:
    //Filter nur anwenden, wenn Parameter vorhanden sind
    //Bedingungen zur Laufzeit zusammenbauen
    //statt viele feste Repository-Methoden zu schreiben
    private Specification<Communication> buildSpec(
            Integer job,
            String type,
            String person,
            CommunicationStatus status,
            LocalDate from
    ) {
        /*
            eine Lambda-Expression, die ein Objekt erzeugt, das das Interface Specification<Communication> implementiert.
            1) Was bedeutet (root, query, cb) -> ...?
            Das ist die Kurzschreibweise für eine Methode mit drei Parametern.
            Diese drei Parameter werden nicht von dir festgelegt, sondern von Spring Data JPA bzw. der JPA-Criteria-API vorgegeben, weil Specification genau so definiert ist.
            Die Lambda sagt also:
            „Wenn du mich später aufrufst, gib mir root, query und cb, und ich liefere dir daraus ein Predicate zurück.“
            2) Warum passt das zu Specification<Communication>?
            Specification ist ein funktionales Interface.
            Das heißt: Es hat genau eine abstrakte Methode.

            Deshalb kann Java diese Schreibweise nutzen:
            (root, query, cb) -> {
                ...
            }

            Statt ausführlicher:
            return new Specification<Communication>() {
                @Override
                public Predicate toPredicate(Root<Communication> root,
                                              CriteriaQuery<?> query,
                                              CriteriaBuilder cb) {
                    ...
                }
            }

            Denn Java weiß:
            3 Parameter
            Rückgabetyp muss Predicate sein
            das ist genau die Methode von Specification

            root, query, cb werden vom JPA-/Spring-Framework beim Ausführen der Specification übergeben.
            Also: Du erstellst nur die Specification
            Spring ruft später intern die Methode auf und übergibt die passenden Objekte

            root: Das ist der Einstiegspunkt auf die Entity.
            query: Die gesamte Criteria-Abfrage. Wird oft in einfachen Specifications gar nicht benutzt, ist aber vorhanden.
            cb: Der CriteriaBuilder. Damit baust du Bedingungen

            Ein Merksatz:
            buildSpec(...) erstellt eine Specification, und die Lambda ist die konkrete Implementierung der toPredicate(...)-Methode

            Spring Data JPA ruft später intern toPredicate(...) auf und übergibt diese Objekte automatisch.
            Also eher so:
            Du baust eine Specification
            Du gibst sie an repository.findAll(spec, pageable)
            Spring ruft intern toPredicate(root, query, cb) auf
            Deine Bedingungen werden in SQL übersetzt
         */
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (job != null) {
                predicates.add(cb.equal(root.get("job").get("id"), job));
                // root.get("job") → the Job entity reference
            }

            if (person != null && !person.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("person")),
                        "%" + person.trim().toLowerCase() + "%"
                ));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (from != null) {
                LocalDateTime fromDateTime = from.atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), fromDateTime));
            }

            if (type != null && !type.isBlank()) {
                Class<? extends Communication> subtype = mapTypeToClass(type.trim());
                predicates.add(cb.equal(root.type(), subtype));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
            // Combine all conditions with AND:
            // Tatsächlich wird hier nicht cb zurückgegeben, sondern:
            //return cb.and(...);  //Also ein Predicate.
        };
    }

    private Class<? extends Communication> mapTypeToClass(String type) {
        return switch (type.toUpperCase()) {
            case "MAIL" -> MailCommunication.class;
            case "PHONE" -> PhoneCommunication.class;
            case "TALK" -> TalkCommunication.class;
            case "TRIAL" -> TrialCommunication.class;
            case "INTERVIEW" -> InterviewCommunication.class;
            case "WEBFORM" -> WebformCommunication.class;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown type: " + type + ". Allowed: MAIL, PHONE, TALK, TRIAL, INTERVIEW, WEBFORM"
            );
        };
    }
}