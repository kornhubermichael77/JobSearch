package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.dto.TimelineItemDTO;
import info.kornhuber.jobsearch.service.TimelineService;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

    // Service-Objekt für Timeline-Generierung.
    private final TimelineService service;

    // DI mit erstelltem "Service-Bean" (vgl. oberhalb!)
    public TimelineController(TimelineService service) { // DI mit generierter Interface-Implementierung
        this.service = service;
    }

    @GetMapping // wird aufgerufen, wenn die URL mit /api/timeline aufgerufen wird
    public Page<TimelineItemDTO> timeline(
            @RequestParam(required = false) Integer jobId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String person,
            @RequestParam(required = false) CommunicationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        // nach Vorbereitung der Parameter wird nun ans Service übergeben:
        return service.timeline(jobId, type, person, status, from, pageable);
    }
}