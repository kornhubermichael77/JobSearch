package info.kornhuber.jobsearch.mapper;

import info.kornhuber.jobsearch.domain.entity.*;
import info.kornhuber.jobsearch.dto.*;
import info.kornhuber.jobsearch.enums.CommunicationType;
import org.springframework.stereotype.Component;

@Component
public class TimelineMapper {

    public TimelineItemDTO toDto(Communication c) {
        if (c instanceof MailCommunication m) {
            MailTimelineDTO dto = new MailTimelineDTO();
            fillBase(dto, m);
            dto.type = CommunicationType.MAIL;
            dto.address = m.getAddress();
            dto.subject = m.getSubject();
            dto.attachments = m.getAttachments();
            dto.direction = m.getDirection();
            return dto;
        }

        if (c instanceof PhoneCommunication p) {
            PhoneTimelineDTO dto = new PhoneTimelineDTO();
            fillBase(dto, p);
            dto.type = CommunicationType.PHONE;
            dto.number = p.getNumber();
            dto.direction = p.getDirection();
            return dto;
        }

        if (c instanceof TalkCommunication t) {
            TalkTimelineDTO dto = new TalkTimelineDTO();
            fillBase(dto, t);
            dto.type = CommunicationType.TALK;
            dto.location = t.getLocation();
            dto.context = t.getContext();
            return dto;
        }

        if (c instanceof TrialCommunication t) {
            TrialTimelineDTO dto = new TrialTimelineDTO();
            fillBase(dto, t);
            dto.type = CommunicationType.TRIAL;
            dto.duration = t.getDuration();
            dto.conclusion = t.getConclusion();
            return dto;
        }

        if (c instanceof InterviewCommunication i) {
            InterviewTimelineDTO dto = new InterviewTimelineDTO();
            fillBase(dto, i);
            dto.type = CommunicationType.INTERVIEW;
            dto.duration = i.getDuration();
            dto.conclusion = i.getConclusion();
            return dto;
        }

        if (c instanceof WebformCommunication w) {
            WebformTimelineDTO dto = new WebformTimelineDTO();
            fillBase(dto, w);
            dto.type = CommunicationType.WEBFORM;
            dto.url = w.getUrl();
            dto.screenshot = w.getScreenshot();
            return dto;
        }

        throw new IllegalArgumentException("Unknown communication type: " + c.getClass().getSimpleName());
    }

    private void fillBase(TimelineItemDTO dto, Communication c) {
        dto.jobId = c.getJob() != null ? c.getJob().getId() : null;
        dto.id = c.getId();
        dto.date = c.getDate();
        dto.person = c.getPerson();
        dto.role = c.getRole();
        dto.content = c.getContent();
        dto.sidemarks = c.getSidemarks();
        dto.status = c.getStatus();
    }
}
