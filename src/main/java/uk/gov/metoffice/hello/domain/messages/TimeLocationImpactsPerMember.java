package uk.gov.metoffice.hello.domain.messages;

import uk.gov.metoffice.hello.domain.ImpactType;

import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.TreeMap;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class TimeLocationImpactsPerMember {
    private String ensembleMember;

    private TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> impacts;

    public TimeLocationImpactsPerMember(String ensembleMember, TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> impacts) {
        this.ensembleMember = ensembleMember;
        this.impacts = impacts;
    }

    public String getEnsembleMember() {
        return ensembleMember;
    }

    public TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> getImpacts() {
        return impacts;
    }
}
