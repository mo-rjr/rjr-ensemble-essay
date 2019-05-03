package uk.gov.metoffice.hello.unit;

import uk.gov.metoffice.hello.message.ImpactType;

import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.TreeMap;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class TimeLocationImpacts {
    private String ensemble;

    private TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> impacts;

    public TimeLocationImpacts(String ensemble, TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> impacts) {
        this.ensemble = ensemble;
        this.impacts = impacts;
    }

    public String getEnsemble() {
        return ensemble;
    }

    public TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> getImpacts() {
        return impacts;
    }
}
