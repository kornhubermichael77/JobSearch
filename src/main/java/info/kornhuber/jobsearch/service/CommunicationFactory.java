package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.service.creator.CommunicationCreator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommunicationFactory {

    // Map mit den CommunicationCreators
    private final Map<CommunicationType, CommunicationCreator> creators;

    // DI mit Liste der CommunicationCreators -> macht Springboot und injiziert
    public CommunicationFactory(
            List<CommunicationCreator> list
    ) {

        creators =
                list.stream()
                        .collect(Collectors.toMap(
                                CommunicationCreator::getType,
                                c -> c
                        ));
    }

    // wird vom CommunicationService aufgerufen, wenn dieses von Controller neue Daten erhält
    public Communication create(
            CreateCommunicationRequest req  // mitgegebene Daten
    ) {
        // Aussuchen und Erstellen des passenden Creators (nach Kommunikations-Typ):
        CommunicationCreator creator = creators.get(req.type);
        if (creator == null) throw new RuntimeException("Unknown type"); // Kommunikationstyp nicht erkannt

        return creator.create(req); // Kommunikation erstellen --> an den passenden Creator delegieren
    }
}