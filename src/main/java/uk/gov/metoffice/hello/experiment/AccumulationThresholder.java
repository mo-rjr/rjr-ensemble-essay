package uk.gov.metoffice.hello.experiment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AccumulationThresholder {

    private final Map<Integer, Float> thresholdsPerBlock;

    private final String fileName;

    public AccumulationThresholder(Map<Integer, Float> thresholdsPerBlock, String fileName) {
        this.thresholdsPerBlock = thresholdsPerBlock;
        this.fileName = fileName;
    }

    public List<Integer> blocksCrossingThresholds(Map<Integer, Float> rawValuesForBlocks) {
        return rawValuesForBlocks.entrySet().stream()
//                .filter(rawDataEntry -> rawDataEntry.getValue() >= thresholdsPerBlock.get(rawDataEntry.getKey()))
                .filter(rawDataEntry -> exceedsThreshold(rawDataEntry.getValue(), thresholdsPerBlock.get(rawDataEntry.getKey())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

    }

    private boolean exceedsThreshold(Float rawData, Float thresholdData) {
        return thresholdData > 0 && rawData > thresholdData;
    }

    public Map<Integer, Float> getThresholdsPerBlock() {
        return thresholdsPerBlock;
    }

    public String getFileName() {
        return fileName;
    }
}
