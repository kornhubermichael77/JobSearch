package info.kornhuber.jobsearch.domain.repository;

import info.kornhuber.jobsearch.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository für Address-Entities.
 */
public interface AddressRepository extends JpaRepository<Address, Integer> {

    /**
     * Liefert alle Adressen einer Company.
     */
    List<Address> findByCompany_Id(Integer companyId);

    /**
     * Liefert alle Adressen eines Users anhand der technischen ownerUserId.
     *
     * Reihenfolge absteigend nach ID, damit neuere Einträge zuerst kommen.
     */
    List<Address> findByOwnerUserIdOrderByIdDesc(Long ownerUserId);
}