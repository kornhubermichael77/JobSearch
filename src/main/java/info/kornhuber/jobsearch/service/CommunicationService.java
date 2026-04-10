package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.domain.entity.*;
import info.kornhuber.jobsearch.dto.CommunicationResponseDTO;
import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.dto.UpdateCommunicationRequest;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.enums.CommunicationDirection;
import info.kornhuber.jobsearch.exception.BadRequestException;
import info.kornhuber.jobsearch.exception.NotFoundException;
import info.kornhuber.jobsearch.mapper.CommunicationMapper;
import info.kornhuber.jobsearch.domain.repository.CommunicationRepository;
import info.kornhuber.jobsearch.domain.repository.JobRepository;
import org.springframework.stereotype.Service;

@Service
public class CommunicationService {

    // Objekte für den Communication-Service
    private final CommunicationFactory communicationFactory;
    private final CommunicationRepository communicationRepository;
    private final JobRepository jobRepository;
    private final CommunicationMapper mapper;

    // DI mit den Objekten für den Communication-Service
    public CommunicationService(
            CommunicationFactory communicationFactory,
            CommunicationRepository communicationRepository,
            JobRepository jobRepository,
            CommunicationMapper mapper
    ) {
        this.communicationFactory = communicationFactory;
        this.communicationRepository = communicationRepository;
        this.jobRepository = jobRepository;
        this.mapper = mapper;
    }

    // wird vom CommunicationController aufgerufen, wenn etwas per POST kommt
    public CommunicationResponseDTO create(CreateCommunicationRequest req) {
        //Communication c = createInstanceByType(req.type);
        Communication c = communicationFactory.create(req);
        // todo: phone.direction ev hier validieren
        applyCommonFields(c, req.jobId, req.date, req.person, req.role, req.content, req.sidemarks, req.status);
        validateTypeSpecificFields(req.type, req.address, req.subject, req.number, req.direction, req.url);
        applySpecificFields(c, req);
        Communication saved = communicationRepository.save(c);
        return mapper.toDto(saved);
    }

    public CommunicationResponseDTO update(Integer id, UpdateCommunicationRequest req) {
        Communication c = communicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Communication not found: " + id));

        CommunicationType existingType = determineType(c);

        applyCommonFieldsForUpdate(c, req.date, req.person, req.role, req.content, req.sidemarks, req.status);
        validateTypeSpecificFields(existingType, req.address, req.subject, req.number, req.direction, req.url);
        applySpecificFieldsForUpdate(c, req);

        Communication saved = communicationRepository.save(c);
        return mapper.toDto(saved);
    }

    public void delete(Integer id) {
        if (!communicationRepository.existsById(id)) {
            throw new NotFoundException("Communication not found: " + id);
        }
        communicationRepository.deleteById(id);
    }

    private CommunicationType determineType(Communication c) {
        if (c instanceof MailCommunication) return CommunicationType.MAIL;
        if (c instanceof PhoneCommunication) return CommunicationType.PHONE;
        if (c instanceof TalkCommunication) return CommunicationType.TALK;
        if (c instanceof TrialCommunication) return CommunicationType.TRIAL;
        if (c instanceof InterviewCommunication) return CommunicationType.INTERVIEW;
        if (c instanceof WebformCommunication) return CommunicationType.WEBFORM;
        throw new BadRequestException("Unsupported communication type");
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
                .orElseThrow(() -> new NotFoundException("Job not found: " + jobId));

        c.setJob(job);
        c.setDate(date);
        c.setPerson(person);
        c.setRole(role);
        c.setContent(content);
        c.setSidemarks(sidemarks);
        c.setStatus(status);
    }

    private void applyCommonFieldsForUpdate(
            Communication c,
            java.time.LocalDateTime date,
            String person,
            String role,
            String content,
            String sidemarks,
            CommunicationStatus status
    ) {
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
    private void applySpecificFieldsForUpdate(Communication c, UpdateCommunicationRequest req) {
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
                    throw new BadRequestException("Bei MAIL muss address gesetzt sein");
                }
            }
            case PHONE -> {
                if (number == null || number.isBlank()) {
                    throw new BadRequestException("Bei PHONE muss number gesetzt sein");
                }
                if (direction == null) {
                    throw new BadRequestException("Bei PHONE muss direction gesetzt sein");
                }
            }
            case WEBFORM -> {
                if (url == null || url.isBlank()) {
                    throw new BadRequestException("Bei WEBFORM muss url gesetzt sein");
                }
            }
            default -> {
            }
        }
    }

    public CommunicationResponseDTO getById(Integer id) {
        Communication c = communicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Communication not found: " + id));
        return mapper.toDto(c);
    }

    /*public Page<Communication> findByJobId(Integer jobId, Pageable pageable) {
        return c_repository.findByJob_Id(jobId, pageable);
    }*/
}
