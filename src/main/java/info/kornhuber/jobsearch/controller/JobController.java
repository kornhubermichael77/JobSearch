package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.dto.CreateJobRequest;
import info.kornhuber.jobsearch.dto.JobResponseDTO;
import info.kornhuber.jobsearch.dto.UpdateJobRequest;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.service.JobService;
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
            @RequestParam(required = false) CommunicationStatus status,
            @RequestParam(required = false) Integer companyId
    ) {
        return service.findAll(status, companyId);
    }

    @GetMapping("/{id}")
    public JobResponseDTO jobById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public JobResponseDTO create(@Valid @RequestBody CreateJobRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public JobResponseDTO modify(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateJobRequest req
    ) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}