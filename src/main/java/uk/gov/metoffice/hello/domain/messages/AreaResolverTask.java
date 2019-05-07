package uk.gov.metoffice.hello.domain.messages;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AreaResolverTask {

    private String contextId;

    private String ensembleId;

    private String adminAreasId;

    private String refToEnsembleSqKmImpacts;

    public AreaResolverTask(String contextId, String ensembleId, String adminAreasId, String refToEnsembleSqKmImpacts) {
        this.contextId = contextId;
        this.ensembleId = ensembleId;
        this.adminAreasId = adminAreasId;
        this.refToEnsembleSqKmImpacts = refToEnsembleSqKmImpacts;
    }

    public String getContextId() {
        return contextId;
    }

    public String getEnsembleId() {
        return ensembleId;
    }

    public String getAdminAreasId() {
        return adminAreasId;
    }

    public String getRefToEnsembleSqKmImpacts() {
        return refToEnsembleSqKmImpacts;
    }
}
