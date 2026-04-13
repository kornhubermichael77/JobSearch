package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.dto.AddressResponseDTO;
import info.kornhuber.jobsearch.dto.CreateAddressRequest;
import info.kornhuber.jobsearch.dto.UpdateAddressRequest;
import info.kornhuber.jobsearch.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für Adressen.
 * API-Design:
 * - Erstellung erfolgt immer in einem klaren Kontext
 *   - Company-Adresse
 *   - aktuelle User-Adresse
 * - Lesen/Ändern/Löschen einer konkreten Adresse erfolgt generisch
 */
@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    /**
     * Erstellt eine Adresse für eine Company.
     */
    @PostMapping("/companies/{companyId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponseDTO createForCompany(
            @PathVariable Integer companyId,
            @Valid @RequestBody CreateAddressRequest req
    ) {
        return service.createForCompany(companyId, req);
    }

    /**
     * Erstellt eine Adresse für einen Job (sowie die zugehörige Firma).
     */
    // neuer Endpunkt!
    @PostMapping("/jobs/{jobId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponseDTO createForJob(
            @PathVariable Integer jobId,
            @Valid @RequestBody CreateAddressRequest req
    ) {
        return service.createForJob(jobId, req);
    }


    /**
     * Erstellt eine Adresse für den aktuell eingeloggten User.
     */
    @PostMapping("/users/me/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponseDTO createForCurrentUser(
            @Valid @RequestBody CreateAddressRequest req
    ) {
        return service.createForCurrentUser(req);
    }

    /**
     * Liefert alle Adressen des aktuell eingeloggten Users.
     */
    @GetMapping("/users/me/addresses")
    public List<AddressResponseDTO> getCurrentUserAddresses() {
        return service.getCurrentUserAddresses();
    }

    /**
     * Liefert eine Adresse anhand ihrer ID.
     */
    @GetMapping("/addresses/{id}")
    public AddressResponseDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    /**
     * Aktualisiert eine bestehende Adresse.
     */
    @PutMapping("/addresses/{id}")
    public AddressResponseDTO update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAddressRequest req
    ) {
        return service.update(id, req);
    }

    /**
     * Löscht eine Adresse anhand ihrer ID.
     */
    @DeleteMapping("/addresses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}