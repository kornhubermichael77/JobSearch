package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.enums.CommunicationStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/communication-status")
// übergibt dem Frontend die erlaubten ENUM-Werte für den Kommunikationsstatus
public class CommunicationStatusController {
    @GetMapping
    public CommunicationStatus[] getAllStatusValues() {
        return CommunicationStatus.values();
    }
}
