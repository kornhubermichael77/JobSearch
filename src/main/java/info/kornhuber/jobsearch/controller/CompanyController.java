package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.dto.CompanyResponseDTO;
import info.kornhuber.jobsearch.dto.CreateCompanyRequest;
import info.kornhuber.jobsearch.dto.UpdateCompanyRequest;
import info.kornhuber.jobsearch.service.CompanyService;
import jakarta.validation.Valid;
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
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CompanyResponseDTO byId(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
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
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}