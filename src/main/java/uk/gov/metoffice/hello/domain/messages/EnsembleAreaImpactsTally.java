package uk.gov.metoffice.hello.domain.messages;

import uk.gov.metoffice.hello.domain.ImpactLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class EnsembleAreaImpactsTally {

    private String contextId;
    private String ensembleId;

    private Map<Integer, Map<ImpactLevel, Integer>> ensembleAreaImpactsTallies = new HashMap<>();

    public EnsembleAreaImpactsTally(String contextId, String ensembleId, Map<Integer, Map<ImpactLevel, Integer>> ensembleAreaImpactsTallies) {
        this.contextId = contextId;
        this.ensembleId = ensembleId;
        this.ensembleAreaImpactsTallies = ensembleAreaImpactsTallies;
    }

    public String getContextId() {
        return contextId;
    }

    public String getEnsembleId() {
        return ensembleId;
    }

    public Map<Integer, Map<ImpactLevel, Integer>> getEnsembleAreaImpactsTallies() {
        return ensembleAreaImpactsTallies;
    }
}
