package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.domain.entity.*;
import info.kornhuber.jobsearch.dto.CommunicationResponseDTO;
import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.enums.CommunicationDirection;
import info.kornhuber.jobsearch.exception.BadRequestException;
import info.kornhuber.jobsearch.exception.NotFoundException;
import info.kornhuber.jobsearch.mapper.CommunicationMapper;
import info.kornhuber.jobsearch.domain.repository.CommunicationRepository;
import info.kornhuber.jobsearch.domain.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommunicationService {

    // Objekte für den Communication-Service
    private final CommunicationFactory c_factory;
    private final CommunicationRepository c_repository;
    private final JobRepository jobRepository;
    private final CommunicationMapper mapper;

    // DI mit den Objekten für den Communication-Service
    public CommunicationService(
            CommunicationFactory c_factory,
            CommunicationRepository c_repository,
            JobRepository jobRepository,
            CommunicationMapper mapper
    ) {
        this.c_factory = c_factory;
        this.c_repository = c_repository;
        this.jobRepository = jobRepository;
        this.mapper = mapper;
    }

    // wird vom CommunicationController aufgerufen, wenn etwas per POST kommt
    public CommunicationResponseDTO create(CreateCommunicationRequest req) {
        //Communication c = createInstanceByType(req.type);
        Communication c = c_factory.create(req);
        // todo: phone.direction ev hier validieren
        applyCommonFields(c, req.jobId, req.date, req.person, req.role, req.content, req.sidemarks, req.status);
        applySpecificFields(c, req);
        Communication saved = c_repository.save(c);
        return mapper.toDto(saved);
    }

    public CommunicationResponseDTO update(Integer id, CreateCommunicationRequest req) {
        Communication c = c_repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Communication not found: " + id));

        CommunicationType existingType = mapper.toDto(c).type;
        if (!existingType.equals(req.type)) {
            throw new BadRequestException("Communication-Typ darf nicht geändert werden");
        }

        applyCommonFields(c, req.jobId, req.date, req.person, req.role, req.content, req.sidemarks, req.status);
        validateTypeSpecificFields(req.type, req.address, req.subject, req.number, req.direction, req.url);
        applySpecificFields(c, req);

        Communication saved = c_repository.save(c);
        return mapper.toDto(saved);
    }

    public void delete(Integer id) {
        if (!c_repository.existsById(id)) {
            throw new RuntimeException("Communication not found: " + id);
        }
        c_repository.deleteById(id);
    }

    private void applyCommonFields(
            Communication c,
            Integer jobId,
            java.time.LocalDateTime date,
            String person,
            String role,
            String content,
            String sidemarks,
            CommunicationStatus status
    ) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        c.setJob(job);
        c.setDate(date);
        c.setPerson(person);
        c.setRole(role);
        c.setContent(content);
        c.setSidemarks(sidemarks);
        c.setStatus(status);
    }

    private void applySpecificFields(Communication c, CreateCommunicationRequest req) {
        if (c instanceof MailCommunication m) {
            m.setAddress(req.address);
            m.setSubject(req.subject);
            m.setAttachments(req.attachments);
            m.setDirection(req.direction);
            return;
        }
        if (c instanceof PhoneCommunication p) {
            p.setNumber(req.number);
            p.setDirection(req.direction);
            return;
        }
        if (c instanceof TalkCommunication t) {
            t.setLocation(req.location);
            t.setContext(req.context);
            return;
        }
        if (c instanceof TrialCommunication t) {
            t.setDuration(req.duration);
            t.setConclusion(req.conclusion);
            return;
        }
        if (c instanceof InterviewCommunication i) {
            i.setDuration(req.duration);
            i.setConclusion(req.conclusion);
            return;
        }
        if (c instanceof WebformCommunication w) {
            w.setUrl(req.url);
            w.setScreenshot(req.screenshot);
        }
    }

    private void validateTypeSpecificFields(
            CommunicationType type,
            String address,
            String subject,
            String number,
            CommunicationDirection direction,
            String url
    ) {
        switch (type) {
            case MAIL -> {
                if (address == null || address.isBlank()) {
                    throw new RuntimeException("Bei MAIL muss address gesetzt sein");
                }
            }
            case PHONE -> {
                if (number == null || number.isBlank()) {
                    throw new RuntimeException("Bei PHONE muss number gesetzt sein");
                }
                if (direction == null) {
                    throw new RuntimeException("Bei PHONE muss direction gesetzt sein");
                }
            }
            case WEBFORM -> {
                if (url == null || url.isBlank()) {
                    throw new RuntimeException("Bei WEBFORM muss url gesetzt sein");
                }
            }
            default -> {
            }
        }
    }

    public CommunicationResponseDTO findById(Integer id) {
        Communication c = c_repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Communication not found: " + id));
        return mapper.toDto(c);
    }

    public void deleteById(Integer id) {
        c_repository.deleteById(id);
    }

    public Communication save(Communication communication) {
        return c_repository.save(communication);
    }

    public Page<Communication> findByJobId(Integer jobId, Pageable pageable) {
        return c_repository.findByJob_Id(jobId, pageable);
    }
}
