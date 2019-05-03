package uk.gov.metoffice.hello.outtray;

import uk.gov.metoffice.hello.message.StormSeverity;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.TreeMap;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class NewEnsembleExceedances {

    private final String ensemble;

    private final TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> thresholdsExceeded;

    public NewEnsembleExceedances(String ensemble,
                                  TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> thresholdsExceeded) {
        this.ensemble = ensemble;
        this.thresholdsExceeded = thresholdsExceeded;
    }

    public String getEnsemble() {
        return ensemble;
    }

    public TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> getThresholdsExceeded() {
        return thresholdsExceeded;
    }
}
