package info.kornhuber.jobsearch.domain.repository;

import info.kornhuber.jobsearch.domain.entity.Job;
import info.kornhuber.jobsearch.domain.repository.projection.JobWithCommunicationCountProjection;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import info.kornhuber.jobsearch.domain.repository.projection.CompanyJobCountProjection;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {

    List<Job> findByStatus(CommunicationStatus status);
    List<Job> findByCompany_Id(Integer companyId);
    List<Job> findByStatusAndCompany_Id(CommunicationStatus status, Integer companyId);


    @Query("""
        select
            j.id as id,
            c.id as companyId,
            c.name as companyName,
            a.id as addressId,
            a.city as city,
            a.street as street,
            a.number as number,
            a.country as country,
            a.postcode as postcode,
            a.headquarter as headquarter,
            a.distance as distance,
            a.traveltime as traveltime,
            j.found as found,
            j.source as source,
            j.url as url,
            j.text as text,
            j.status as status,
            j.mail as mail,
            j.mailPerson as mailPerson,
            j.tel as tel,
            j.telPerson as telPerson,
            j.teilzeit as teilzeit,
            j.gleitzeit as gleitzeit,
            j.homeoffice as homeoffice,
            j.features as features,
            count(comm.id) as communicationCount
        from Job j
        left join j.company c
        left join j.address a
        left join Communication comm on comm.job.id = j.id
        where (:status is null or j.status = :status)
          and (:companyId is null or c.id = :companyId)
        group by
            j.id, c.id, c.name, a.id, a.city, a.street, a.number, a.postcode, a.country, a.headquarter, a.distance, a.traveltime,
            j.found, j.source, j.url, j.text, j.status,
            j.mail, j.mailPerson, j.tel, j.telPerson,
            j.teilzeit, j.gleitzeit, j.homeoffice, j.features
        order by j.found desc
    """)
    List<JobWithCommunicationCountProjection> findAllWithCommunicationCount(
            @Param("status") CommunicationStatus status,
            @Param("companyId") Integer companyId
    );

    @Query("""
    select
        j.company.id as companyId,
        count(j.id) as jobCount
    from Job j
    where j.company.id in :companyIds
    group by j.company.id
""")
    List<CompanyJobCountProjection> countJobsByCompanyIds(@Param("companyIds") List<Integer> companyIds);

    long countByCompany_Id(Integer companyId);
}