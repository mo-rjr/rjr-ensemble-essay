package uk.gov.metoffice.hello.outtray;

import uk.gov.metoffice.hello.message.StormSeverity;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class EnsembleExceedances {

    private String ensemble;

    private TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> thresholdsExceeded = new TreeMap<>();

    public EnsembleExceedances(String ensemble, TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> thresholdsExceeded) {
        this.ensemble = ensemble;
        this.thresholdsExceeded = thresholdsExceeded;
    }

    public String getEnsemble() {
        return ensemble;
    }

    public void setEnsemble(String ensemble) {
        this.ensemble = ensemble;
    }

    public Map<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> getThresholdsExceeded() {
        return thresholdsExceeded;
    }

    public void setThresholdsExceeded(TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> thresholdsExceeded) {
        this.thresholdsExceeded = thresholdsExceeded;
    }

    @Override
    public String toString() {
        return "ensemble " + ensemble +
                ", data: " + thresholdsExceeded.entrySet()
                .stream()
                .map(this::oneEntryToString)
                .collect(Collectors.joining("\n"));
    }

    private String oneEntryToString(Map.Entry<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> zonedDateTimeEnumMapEntry) {
        String enumMapString = zonedDateTimeEnumMapEntry.getValue().entrySet().stream()
                .map(this::severityMapToString)
                .collect(Collectors.joining("\n"));
        return zonedDateTimeEnumMapEntry.getKey() + "\n" + enumMapString;
    }

    private String severityMapToString(Map.Entry<StormSeverity, List<Integer>> stormSeverityListEntry) {
        int entryCount = stormSeverityListEntry.getValue().size();
        String message = "    " + stormSeverityListEntry.getKey() + " has " + (entryCount == 1 ? "1 entry" : entryCount + " entries");
        if (stormSeverityListEntry.getValue().size() < 10) {
            message += "\n      ";
            message += stormSeverityListEntry.getValue()
                    .stream()
                    .map(i -> Integer.toString(i))
                    .collect(Collectors.joining(","));
        }

        return message;
    }
}
