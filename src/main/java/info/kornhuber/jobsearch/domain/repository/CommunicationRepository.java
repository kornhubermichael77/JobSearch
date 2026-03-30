package info.kornhuber.jobsearch.domain.repository;

import info.kornhuber.jobsearch.domain.entity.Communication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommunicationRepository
        extends JpaRepository<Communication, Integer>, JpaSpecificationExecutor<Communication> {


    //@EntityGraph(attributePaths = "jobId")
    Page<Communication> findByJob_Id(Integer job, Pageable pageable);
    // zB im Controller: Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending()
    // Pageable sind die Auftragsdetails, wie aus der ganzen Ergebnisliste eine Seite
    // mit Ergebnissen extrahiert werden soll!

}