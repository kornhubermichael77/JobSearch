package info.kornhuber.jobsearch.domain.repository;

import info.kornhuber.jobsearch.domain.entity.Company;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import info.kornhuber.jobsearch.domain.repository.projection.CompanyWithJobCountProjection;
import org.springframework.data.jpa.repository.Query;

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

    @Query("""
        select
            c.id as id,
            c.name as name,
            c.mail as mail,
            c.mailPerson as mailPerson,
            c.tel as tel,
            c.telPerson as telPerson,
            c.summary as summary,
            c.url as url,
            c.urlJobs as urlJobs,
            count(j.id) as jobCount
        from Company c
        left join c.jobs j
        group by c.id, c.name, c.mail, c.mailPerson, c.tel, c.telPerson, c.summary, c.url, c.urlJobs
        order by c.name asc
    """)
    List<CompanyWithJobCountProjection> findAllWithJobCount();

}