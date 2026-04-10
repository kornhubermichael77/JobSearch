package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.dto.UpdateCommunicationRequest;
import info.kornhuber.jobsearch.dto.CommunicationResponseDTO;
import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.service.CommunicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communications")
public class CommunicationController {

    private final CommunicationService service;

    public CommunicationController(CommunicationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public CommunicationResponseDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommunicationResponseDTO create(@Valid @RequestBody CreateCommunicationRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public CommunicationResponseDTO update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCommunicationRequest req
    ) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}