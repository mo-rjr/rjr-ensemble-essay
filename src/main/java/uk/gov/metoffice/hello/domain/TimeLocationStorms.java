package uk.gov.metoffice.hello.domain;

import java.time.ZonedDateTime;
import java.util.TreeMap;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class TimeLocationStorms {

    private final String ensemble;

    private final TreeMap<ZonedDateTime, TreeMap<Integer, StormReturnPeriod>> storms;

    public TimeLocationStorms(String ensemble, TreeMap<ZonedDateTime, TreeMap<Integer, StormReturnPeriod>> storms) {
        this.ensemble = ensemble;
        this.storms = storms;
    }

    public String getEnsemble() {
        return ensemble;
    }

    public TreeMap<ZonedDateTime, TreeMap<Integer, StormReturnPeriod>> getStorms() {
        return storms;
    }
}
