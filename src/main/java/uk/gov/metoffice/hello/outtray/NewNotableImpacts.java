package uk.gov.metoffice.hello.outtray;

import uk.gov.metoffice.hello.domain.ImpactType;
import uk.gov.metoffice.hello.domain.StormSeverity;

import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.TreeMap;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class NewNotableImpacts {

    private final String ensemble;

    private final TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<StormSeverity, EnumMap<ImpactType, Short>>>> impacts;

    public NewNotableImpacts(String ensemble, TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<StormSeverity, EnumMap<ImpactType, Short>>>> impacts) {
        this.ensemble = ensemble;
        this.impacts = impacts;
    }

    public String getEnsemble() {
        return ensemble;
    }

    public TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<StormSeverity, EnumMap<ImpactType, Short>>>> getImpacts() {
        return impacts;
    }
}
