package info.kornhuber.jobsearch.mapper;

import info.kornhuber.jobsearch.domain.entity.*;
import info.kornhuber.jobsearch.dto.CommunicationResponseDTO;
import info.kornhuber.jobsearch.enums.CommunicationType;
import org.springframework.stereotype.Component;

@Component
public class CommunicationMapper {

    public CommunicationResponseDTO toDto(Communication c) {
        CommunicationResponseDTO dto = new CommunicationResponseDTO();

        dto.id = c.getId();
        dto.jobId = c.getJob() != null ? c.getJob().getId() : null;
        dto.date = c.getDate();
        dto.person = c.getPerson();
        dto.role = c.getRole();
        dto.content = c.getContent();
        dto.sidemarks = c.getSidemarks();
        dto.status = c.getStatus();

        if (c instanceof MailCommunication m) {
            dto.type = CommunicationType.MAIL;
            dto.address = m.getAddress();
            dto.subject = m.getSubject();
            dto.attachments = m.getAttachments();
            dto.direction = m.getDirection();
            return dto;
        }

        if (c instanceof PhoneCommunication p) {
            dto.type = CommunicationType.PHONE;
            dto.number = p.getNumber();
            dto.direction = p.getDirection();
            return dto;
        }

        if (c instanceof TalkCommunication t) {
            dto.type = CommunicationType.TALK;
            dto.location = t.getLocation();
            dto.context = t.getContext();
            return dto;
        }

        if (c instanceof TrialCommunication t) {
            dto.type = CommunicationType.TRIAL;
            dto.duration = t.getDuration();
            dto.conclusion = t.getConclusion();
            return dto;
        }

        if (c instanceof InterviewCommunication i) {
            dto.type = CommunicationType.INTERVIEW;
            dto.duration = i.getDuration();
            dto.conclusion = i.getConclusion();
            return dto;
        }

        if (c instanceof WebformCommunication w) {
            dto.type = CommunicationType.WEBFORM;
            dto.url = w.getUrl();
            dto.screenshot = w.getScreenshot();
            return dto;
        }

        throw new IllegalArgumentException("Unknown communication type: " + c.getClass().getSimpleName());
    }
}