package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.domain.repository.projection.CompanyWithJobCountProjection;
import info.kornhuber.jobsearch.dto.CompanyResponseDTO;
import info.kornhuber.jobsearch.dto.CreateCompanyRequest;
import info.kornhuber.jobsearch.dto.UpdateCompanyRequest;
import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.mapper.CompanyMapper;
import info.kornhuber.jobsearch.domain.repository.CompanyRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyService(
            CompanyRepository companyRepository,
            CompanyMapper companyMapper
    ) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO findById(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found: " + id));

        return companyMapper.toDto(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> findAll() {
        return companyRepository.findAllWithJobCount().stream()
                .map(p -> {
                    CompanyResponseDTO dto = new CompanyResponseDTO();
                    dto.id = p.getId();
                    dto.name = p.getName();
                    dto.mail = p.getMail();
                    dto.mailPerson = p.getMailPerson();
                    dto.tel = p.getTel();
                    dto.telPerson = p.getTelPerson();
                    dto.summary = p.getSummary();
                    dto.url = p.getUrl();
                    dto.urlJobs = p.getUrlJobs();
                    dto.jobCount = p.getJobCount();

                    // bewusst KEINE addresses hier -> List View schlank halten
                    dto.addresses = List.of();

                    return dto;
                })
                .toList();
    }

    public CompanyResponseDTO create(CreateCompanyRequest req) {
        Company company = new Company();
        applyFields(company, req);

        Company saved = companyRepository.save(company);
        return companyMapper.toDto(saved);
    }

    public CompanyResponseDTO update(Integer id, UpdateCompanyRequest req) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found: " + id));

        applyFields(company, req);

        Company saved = companyRepository.save(company);
        return companyMapper.toDto(saved);
    }

    public void delete(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found: " + id));

        companyRepository.delete(company);
    }

    private void applyFields(Company company, CreateCompanyRequest req) {
        company.setName(req.name);
        company.setMail(req.mail);
        company.setMailPerson(req.mailPerson);
        company.setTel(req.tel);
        company.setTelPerson(req.telPerson);
        company.setSummary(req.summary);
        company.setUrl(req.url);
        company.setUrlJobs(req.urlJobs);
    }

    private void applyFields(Company company, UpdateCompanyRequest req) {
        company.setName(req.name);
        company.setMail(req.mail);
        company.setMailPerson(req.mailPerson);
        company.setTel(req.tel);
        company.setTelPerson(req.telPerson);
        company.setSummary(req.summary);
        company.setUrl(req.url);
        company.setUrlJobs(req.urlJobs);
    }
}