package info.kornhuber.jobsearch.service.creator;

import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.enums.CommunicationType;

public interface CommunicationCreator {

    // Jede Implementierung muss eine Typenbezeichnung liefern
    CommunicationType getType();

    // Jede Implementierung muss eine Communication erstellen können
    Communication create(CreateCommunicationRequest req);
}
