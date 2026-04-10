package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.dto.CompanyResponseDTO;
import info.kornhuber.jobsearch.dto.CreateCompanyRequest;
import info.kornhuber.jobsearch.dto.UpdateCompanyRequest;
import info.kornhuber.jobsearch.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @GetMapping
    public List<CompanyResponseDTO> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CompanyResponseDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponseDTO create(@Valid @RequestBody CreateCompanyRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public CompanyResponseDTO update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCompanyRequest req
    ) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}