package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.dto.*;
import info.kornhuber.jobsearch.enums.JobStatus;
import info.kornhuber.jobsearch.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService service;

    public JobController(JobService service) {
        this.service = service;
    }

    @GetMapping
    public List<JobResponseDTO> all(
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) Integer companyId
    ) {
        return service.getAll(status, companyId);
    }

    /**
     * Liefert reduzierte Job-Daten für den Job-Filter.
     *
     * API-Design:
     * Der Endpunkt heißt bewusst "options" statt "for-filter",
     * weil er damit eine darstellungsbezogene Ressource beschreibt
     * und nicht den internen UI-Zweck des Frontends.
     */
    @GetMapping("/options")
    public List<JobsForFilterResponseDTO> getJobOptions(
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) Integer companyId
    ) {
        return service.getAllForFilter(status, companyId);
    }

    @GetMapping("/{id}")
    public JobResponseDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    /**
     * Erstellt einen neuen Job.
     *
     * HTTP 201 signalisiert dem Client, dass eine neue Ressource angelegt wurde.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponseDTO create(@Valid @RequestBody CreateJobRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public JobResponseDTO update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateJobRequest req
    ) {
        return service.update(id, req);
    }

    /**
     * Aktualisiert die dem Job zugewiesene Adresse.
     *
     * API-Design:
     * Die Ressource in der URL ist "address", nicht das technische Feld "addressId".
     * Die konkrete Zieladresse wird weiterhin im Request-Body übergeben.
     */
    @PatchMapping("/{id}/address")
    public JobResponseDTO updateJobAddress(
            @PathVariable Integer id,
            @RequestBody UpdateJobAddressRequest req
    ) {
        return service.updateJobAddress(id, req);
    }

    /**
     * Löscht einen Job.
     *
     * HTTP 204 bedeutet:
     * Die Löschung war erfolgreich und der Response-Body ist leer.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}