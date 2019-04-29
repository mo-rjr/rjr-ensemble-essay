package uk.gov.metoffice.hello.unit;

import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class Thresholder {

    private static final Map<StormDuration, Map<StormSeverity, String>> SURFACE_RUNOFF_THRESHOLD_FILES = new HashMap<>();

    static {
        String root = "U:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\";
        Map<StormSeverity, String> oneHourFiles = new HashMap<>();
        oneHourFiles.put(StormSeverity.THIRTY_YEARS, root + "p30_1hr_a.bil");
        oneHourFiles.put(StormSeverity.ONE_HUNDRED_YEARS, root + "p100_1hr_a.bil");
        oneHourFiles.put(StormSeverity.ONE_THOUSAND_YEARS, root + "p1000_1hr_a.bil");

        Map<StormSeverity, String> threeHourFiles = new HashMap<>();
        threeHourFiles.put(StormSeverity.THIRTY_YEARS, root + "p30_3hr_a.bil");
        threeHourFiles.put(StormSeverity.ONE_HUNDRED_YEARS, root + "p100_3hr_a.bil");
        threeHourFiles.put(StormSeverity.ONE_THOUSAND_YEARS, root + "p1000_3hr_a.bil");

        Map<StormSeverity, String> sixHourFiles = new HashMap<>();
        sixHourFiles.put(StormSeverity.THIRTY_YEARS, root + "p30_6hr_a.bil");
        sixHourFiles.put(StormSeverity.ONE_HUNDRED_YEARS, root + "p100_6r_a.bil");
        sixHourFiles.put(StormSeverity.ONE_THOUSAND_YEARS, root + "p1000_6hr_a.bil");

        SURFACE_RUNOFF_THRESHOLD_FILES.put(StormDuration.ONE_HOUR, oneHourFiles);
        SURFACE_RUNOFF_THRESHOLD_FILES.put(StormDuration.THREE_HOURS, threeHourFiles);
        SURFACE_RUNOFF_THRESHOLD_FILES.put(StormDuration.SIX_HOURS, sixHourFiles);
    }

    private final ReadValuesForBlocks<Float> readValuesForBlocks;

    public Thresholder(ReadValuesForBlocks<Float> readValuesForBlocks) {
        this.readValuesForBlocks = readValuesForBlocks;
    }


    public Map<ZonedDateTime, Map<Integer, Boolean>> threshold(StormDuration stormDuration,
                                                               StormSeverity stormSeverity,
                                                               List<Integer> blocks,
                                                               Map<ZonedDateTime, Map<Integer, Float>> timestepsToBlockValues) {
        String fileName = SURFACE_RUNOFF_THRESHOLD_FILES.get(stormDuration).get(stormSeverity);
        Map<Integer, Float> thresholdValues = readValuesForBlocks.blockValuesFromFile(fileName, blocks);

        if (thresholdValues == null) {
            throw new RuntimeException("No threshold values");
        }

        return checkForThresholdsCrossed(thresholdValues, timestepsToBlockValues);

    }

    private Map<ZonedDateTime, Map<Integer, Boolean>> checkForThresholdsCrossed(Map<Integer, Float> thresholdValues,
                                                                                Map<ZonedDateTime, Map<Integer, Float>> timestepsToBlockValues) {
        Map<ZonedDateTime, Map<Integer, Boolean>> thresholdsCrossed = new HashMap<>();
        for (Map.Entry<ZonedDateTime, Map<Integer, Float>> entry : timestepsToBlockValues.entrySet()) {
            thresholdsCrossed.put(entry.getKey(), applyThresholds(thresholdValues, entry.getValue()));
        }
        return thresholdsCrossed;
    }


    private Map<Integer, Boolean> applyThresholds(Map<Integer, Float> thresholds, Map<Integer, Float> actualValues) {
        Map<Integer, Boolean> thresholdsApplied = new HashMap<>();
        for (Map.Entry<Integer, Float> entry : actualValues.entrySet()) {
            Float threshold = thresholds.getOrDefault(entry.getKey(), -99.0f);
            Float value = entry.getValue();
            Boolean output = bothValuesExistAndActualIsGreater(threshold, value);
            thresholdsApplied.put(entry.getKey(), output);
        }
        return thresholdsApplied;

    }

    private boolean bothValuesExistAndActualIsGreater(Float thresholdValue, Float actualValue) {
        return thresholdValue > 0 && actualValue > 0 && thresholdValue <= actualValue;
    }

}
