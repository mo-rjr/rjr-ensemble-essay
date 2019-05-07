package uk.gov.metoffice.hello.explode.thresholds;

import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormSeverity;
import uk.gov.metoffice.hello.explode.AdvancingBilFileReader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.metoffice.hello.domain.BilConstants.ROW_LENGTH;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AccumulationThresholdProvider {

    private static final String DURATION_HOURS_KEY = "${DURATION_HOURS}";
    private static final String SEVERITY_YEARS_KEY = "${SEVERITY_YEARS}";
    private static final String FILE_TEMPLATE = "p" + SEVERITY_YEARS_KEY + "_" + DURATION_HOURS_KEY + "hr_a.bil";

    private final Map<StormDuration, AccumulationThresholder> accumulationThresholders = new HashMap<>();

    private final String thresholdFolderRoot;

    private final List<Integer> validBlocks;

    public AccumulationThresholdProvider(String thresholdFolderRoot, List<Integer> validBlocks) {
        this.thresholdFolderRoot = thresholdFolderRoot;
        this.validBlocks = validBlocks;
    }

    // lazily populates map
    public AccumulationThresholder getFor(StormDuration stormDuration) {
        return accumulationThresholders.computeIfAbsent(stormDuration,
                duration -> createAccumulationThresholderFor(stormDuration));
    }

    private AccumulationThresholder createAccumulationThresholderFor(StormDuration stormDuration) {
        AccumulationThresholder accumulationThresholder = new AccumulationThresholder(stormDuration);

        for (StormSeverity stormSeverity : StormSeverity.values()) {
            String fileName = thresholdFolderRoot + fileNameFor(stormDuration, stormSeverity);
            try (AdvancingBilFileReader<Float> bilFileReader = AdvancingBilFileReader.forFloats(fileName, ROW_LENGTH)) {
                Map<Integer, Float> thresholdValues = new HashMap<>();
                for (Integer block : validBlocks) {
                    thresholdValues.put(block, bilFileReader.readNext(block));
                }
                accumulationThresholder.put(stormSeverity, thresholdValues);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
                // todo think about this pretty hard
            }
        }
        return accumulationThresholder;
    }

    private String fileNameFor(StormDuration stormDuration, StormSeverity stormSeverity) {
        String durationHours = Integer.toString(stormDuration.getHours());
        String stormYears = Integer.toString(stormSeverity.getYears());
        return FILE_TEMPLATE.replace(DURATION_HOURS_KEY, durationHours).replace(SEVERITY_YEARS_KEY, stormYears);
    }
}
