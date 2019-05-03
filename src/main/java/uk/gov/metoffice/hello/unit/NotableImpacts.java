package uk.gov.metoffice.hello.unit;

import uk.gov.metoffice.hello.message.ImpactType;
import uk.gov.metoffice.hello.message.StormSeverity;

import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class NotableImpacts {

    private String ensemble;

    private TreeMap<ZonedDateTime, EnumMap<StormSeverity, Map<Integer, EnumMap<ImpactType, Short>>>> impacts = new TreeMap<>();

    public NotableImpacts(String ensemble, TreeMap<ZonedDateTime, EnumMap<StormSeverity, Map<Integer, EnumMap<ImpactType, Short>>>> impacts) {
        this.ensemble = ensemble;
        this.impacts = impacts;
    }

    public String getEnsemble() {
        return ensemble;
    }

    public TreeMap<ZonedDateTime, EnumMap<StormSeverity, Map<Integer, EnumMap<ImpactType, Short>>>> getImpacts() {
        return impacts;
    }
}
