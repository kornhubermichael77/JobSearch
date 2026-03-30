package info.kornhuber.jobsearch.service.creator;

import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.domain.entity.PhoneCommunication;
import info.kornhuber.jobsearch.enums.CommunicationType;
import org.springframework.stereotype.Service;

@Service
public class PhoneCommunicationCreator
        implements CommunicationCreator {

    @Override
    public CommunicationType getType() { return CommunicationType.PHONE; }

    // Wird vom CommunicationFactory aufgerufen, wenn dieser eine Telefon-Kommunikation erstellen soll
    @Override
    public Communication create(
            CreateCommunicationRequest req
    ) {
        PhoneCommunication p = new PhoneCommunication();
        p.setNumber(req.number);
        p.setDirection(req.direction);
        return p;
    }
}