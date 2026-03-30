package info.kornhuber.jobsearch.domain.repository;

import info.kornhuber.jobsearch.domain.entity.Company;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    @Override
    @EntityGraph(attributePaths = "addresses")
    // Damit werden bei genau diesen beiden Aufrufen die addresses direkt mitgeladen.
    List<Company> findAll();

    @Override
    @EntityGraph(attributePaths = "addresses")
    Optional<Company> findById(Integer id);

}