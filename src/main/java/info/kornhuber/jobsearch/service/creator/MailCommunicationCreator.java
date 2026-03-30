package info.kornhuber.jobsearch.service.creator;

import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.domain.entity.MailCommunication;
import info.kornhuber.jobsearch.enums.CommunicationType;
import org.springframework.stereotype.Service;

@Service
public class MailCommunicationCreator
        implements CommunicationCreator {

    @Override
    public CommunicationType getType() {
        return CommunicationType.MAIL;
    }

    // Wird vom CommunicationFactory aufgerufen, wenn dieser eine Mail-Kommunikation erstellen soll
    @Override
    public Communication create(
            CreateCommunicationRequest req
    ) {
        MailCommunication m = new MailCommunication();
        m.setAddress(req.address);
        m.setSubject(req.subject);
        m.setAttachments(req.attachments);
        m.setDirection(req.direction);
        return m;
    }
}