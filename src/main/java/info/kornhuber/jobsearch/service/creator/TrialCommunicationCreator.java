package info.kornhuber.jobsearch.service.creator;

import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.domain.entity.TrialCommunication;
import info.kornhuber.jobsearch.enums.CommunicationType;
import org.springframework.stereotype.Service;

@Service
public class TrialCommunicationCreator
        implements CommunicationCreator {

    @Override
    public CommunicationType getType() { return CommunicationType.TRIAL; }

    // Wird vom CommunicationFactory aufgerufen, wenn dieser eine Trial-Kommunikation erstellen soll
    @Override
    public Communication create(
            CreateCommunicationRequest req
    ) {
        TrialCommunication p = new TrialCommunication();
        p.setDuration(req.duration);
        p.setConclusion(req.conclusion);
        return p;
    }
}