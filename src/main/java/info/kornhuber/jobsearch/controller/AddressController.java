package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.dto.AddressResponseDTO;
import info.kornhuber.jobsearch.dto.CreateAddressRequest;
import info.kornhuber.jobsearch.dto.UpdateAddressRequest;
import info.kornhuber.jobsearch.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @PostMapping("/api/companies/{companyId}/addresses")
    public AddressResponseDTO createForCompany(
            @PathVariable Integer companyId,
            @Valid @RequestBody CreateAddressRequest req
    ) {
        return service.createForCompany(companyId, req);
    }

    @GetMapping("/api/addresses/{id}")
    public AddressResponseDTO byId(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PutMapping("/api/addresses/{id}")
    public AddressResponseDTO update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAddressRequest req
    ) {
        return service.update(id, req);
    }

    @DeleteMapping("/api/addresses/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
