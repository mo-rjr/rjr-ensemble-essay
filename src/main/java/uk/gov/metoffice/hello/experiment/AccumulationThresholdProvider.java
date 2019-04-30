package uk.gov.metoffice.hello.experiment;

import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.metoffice.hello.Main.ROW_LENGTH;

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

    private Map<StormDuration, Map<StormSeverity, AccumulationThresholder>> accumulationThresholders = new HashMap<>();

    private final String thresholdFolderRoot;

    private final List<Integer> validBlocks;

    public AccumulationThresholdProvider(String thresholdFolderRoot, List<Integer> validBlocks) {
        this.thresholdFolderRoot = thresholdFolderRoot;
        this.validBlocks = validBlocks;
    }

    // lazily populates map
    public AccumulationThresholder getFor(StormDuration stormDuration, StormSeverity stormSeverity) {
        Map<StormSeverity, AccumulationThresholder> forThisDuration = accumulationThresholders.computeIfAbsent(stormDuration,
                x -> new HashMap<>());
        return forThisDuration.computeIfAbsent(stormSeverity,
                severity -> createAccumulationThresholderFor(stormDuration, severity));
    }

    private AccumulationThresholder createAccumulationThresholderFor(StormDuration stormDuration, StormSeverity stormSeverity) {
        String fileName = fileNameFor(stormDuration, stormSeverity);
        try (AdvancingBilFileReader<Float> advancingBilFileReader =
                     new AdvancingBilFileReader<>(ByteBuffer::getFloat, thresholdFolderRoot + fileName,
                             ROW_LENGTH, Float.BYTES)) {

            Map<Integer, Float> thresholdValues = new HashMap<>();
            for (Integer block: validBlocks) {
                thresholdValues.put(block, advancingBilFileReader.readNext(block));
            }
            return new AccumulationThresholder(thresholdValues, fileName);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
            // todo think about this pretty hard
        }

    }

    private String fileNameFor(StormDuration stormDuration, StormSeverity stormSeverity) {
        String durationHours = Integer.toString(stormDuration.getHours());
        String stormYears = Integer.toString(stormSeverity.getYears());
        return FILE_TEMPLATE.replace(DURATION_HOURS_KEY, durationHours).replace(SEVERITY_YEARS_KEY, stormYears);
    }
}
