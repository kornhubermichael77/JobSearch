package info.kornhuber.jobsearch.service.creator;

import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.domain.entity.TalkCommunication;
import info.kornhuber.jobsearch.enums.CommunicationType;
import org.springframework.stereotype.Service;

@Service
public class TalkCommunicationCreator
        implements CommunicationCreator {

    @Override
    public CommunicationType getType() {
        return CommunicationType.TALK;
    }

    // Wird vom CommunicationFactory aufgerufen, wenn dieser eine Talk-Kommunikation erstellen soll
    @Override
    public Communication create(
            CreateCommunicationRequest req
    ) {
        TalkCommunication p = new TalkCommunication();
        p.setLocation(req.location);
        p.setContext(req.context);
        return p;
    }
}