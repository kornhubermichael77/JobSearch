package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.domain.repository.CompanyRepository;
import info.kornhuber.jobsearch.domain.repository.JobRepository;
import info.kornhuber.jobsearch.domain.repository.projection.CompanyJobCountProjection;
import info.kornhuber.jobsearch.dto.CompanyResponseDTO;
import info.kornhuber.jobsearch.dto.CreateCompanyRequest;
import info.kornhuber.jobsearch.dto.UpdateCompanyRequest;
import info.kornhuber.jobsearch.mapper.CompanyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import info.kornhuber.jobsearch.exception.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final JobRepository jobRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            CompanyMapper companyMapper,
            JobRepository jobRepository
    ) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.jobRepository = jobRepository;
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO getById(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found: " + id));

        CompanyResponseDTO dto = companyMapper.toDto(company);
        dto.jobCount = jobRepository.countByCompany_Id(company.getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getAll() {
        List<Company> companies = companyRepository.findAll();

        if (companies.isEmpty()) {
            return List.of();
        }

        List<Integer> companyIds = companies.stream()
                .map(Company::getId)
                .toList();

        Map<Integer, Long> jobCountByCompanyId = jobRepository.countJobsByCompanyIds(companyIds).stream()
                .collect(Collectors.toMap(
                        CompanyJobCountProjection::getCompanyId,
                        CompanyJobCountProjection::getJobCount
                ));

        return companies.stream()
                .map(company -> {
                    CompanyResponseDTO dto = companyMapper.toDto(company);
                    dto.jobCount = jobCountByCompanyId.getOrDefault(company.getId(), 0L);
                    return dto;
                })
                .toList();
    }

    public CompanyResponseDTO create(CreateCompanyRequest req) {
        Company company = new Company();
        applyFields(company, req);

        Company saved = companyRepository.save(company);
        CompanyResponseDTO dto = companyMapper.toDto(saved);
        dto.jobCount = 0L;
        return dto;
    }

    public CompanyResponseDTO update(Integer id, UpdateCompanyRequest req) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found: " + id));

        applyFields(company, req);

        Company saved = companyRepository.save(company);
        CompanyResponseDTO dto = companyMapper.toDto(saved);
        dto.jobCount = jobRepository.countByCompany_Id(saved.getId());
        return dto;
    }

    public void delete(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found: " + id));

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