package info.kornhuber.jobsearch.service.creator;

import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.domain.entity.WebformCommunication;
import info.kornhuber.jobsearch.enums.CommunicationType;
import org.springframework.stereotype.Service;

@Service
public class WebformCommunicationCreator
        implements CommunicationCreator {

    @Override
    public CommunicationType getType() { return CommunicationType.WEBFORM; }

    // Wird vom CommunicationFactory aufgerufen, wenn dieser eine Webform-Kommunikation erstellen soll
    @Override
    public Communication create(
            CreateCommunicationRequest req
    ) {
        WebformCommunication p = new WebformCommunication();
        p.setUrl(req.url);
        p.setScreenshot(req.screenshot);
        return p;
    }
}
