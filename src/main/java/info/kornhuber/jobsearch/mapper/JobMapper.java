package info.kornhuber.jobsearch.mapper;

import info.kornhuber.jobsearch.dto.JobResponseDTO;
import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.domain.entity.Job;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public JobResponseDTO toDto(Job job) {
        JobResponseDTO dto = new JobResponseDTO();

        dto.id = job.getId();

        Company company = job.getCompany();
        if (company != null) {
            dto.companyId = company.getId();
            dto.companyName = company.getName();
        }

        Address address = job.getAddress();
        if (address != null) {
            dto.addressId = address.getId();
            dto.city = address.getCity();
            dto.street = address.getStreet();
            dto.number = address.getNumber();
            dto.postcode = address.getPostcode();
            dto.country = address.getCountry();
            dto.headquarter = address.getHeadquarter();
            dto.distance = address.getDistance();
            dto.traveltime = address.getTraveltime();
        }

        dto.found = job.getFound();
        dto.source = job.getSource();
        dto.url = job.getUrl();
        dto.text = job.getText();
        dto.status = job.getStatus();

        dto.mail = job.getMail();
        dto.mailPerson = job.getMailPerson();
        dto.tel = job.getTel();
        dto.telPerson = job.getTelPerson();

        dto.teilzeit = job.getTeilzeit();
        dto.gleitzeit = job.getGleitzeit();
        dto.homeoffice = job.getHomeoffice();
        dto.features = job.getFeatures();

        dto.communicationCount = 0L;
        return dto;
    }
}