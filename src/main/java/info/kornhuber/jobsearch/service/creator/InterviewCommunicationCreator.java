package info.kornhuber.jobsearch.service.creator;

import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.domain.entity.InterviewCommunication;
import info.kornhuber.jobsearch.enums.CommunicationType;
import org.springframework.stereotype.Service;

@Service
public class InterviewCommunicationCreator
        implements CommunicationCreator {

    @Override
    public CommunicationType getType() {return CommunicationType.INTERVIEW; }

    // Wird vom CommunicationFactory aufgerufen, wenn dieser eine Interview-Kommunikation erstellen soll
    @Override
    public Communication create(
            CreateCommunicationRequest req
    ) {
        InterviewCommunication p = new InterviewCommunication();
        p.setDuration(req.duration);
        p.setConclusion(req.conclusion);
        return p;
    }
}