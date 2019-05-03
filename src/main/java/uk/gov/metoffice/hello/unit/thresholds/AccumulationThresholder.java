package uk.gov.metoffice.hello.unit.thresholds;

import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AccumulationThresholder {

    private final StormDuration stormDuration;

    private final EnumMap<StormSeverity, Map<Integer, Float>> thresholdsPerBlockPerStormSeverity = new EnumMap<>(StormSeverity.class);

    public AccumulationThresholder(StormDuration stormDuration) {
        this.stormDuration = stormDuration;
    }

    public TreeMap<Integer, List<StormSeverity>> exceededThresholds(TreeMap<Integer, Float> accumulatedData) {
        Map<Integer, List<StormSeverity>> exceededThresholds = accumulatedData.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> stormSeveritiesForBlock(entry.getKey(), entry.getValue())));
        // now get rid of any blocks with an empty list of exceeded thresholds
        return exceededThresholds.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new));
    }

    public List<StormSeverity> stormSeveritiesForBlock(Integer block, Float rawValue) {
        return Arrays.stream(StormSeverity.values())
                .filter(severity -> thresholdCrossedAtThisSeverity(severity, block, rawValue))
                .collect(Collectors.toList());
    }

    private boolean thresholdCrossedAtThisSeverity(StormSeverity stormSeverity, Integer block, Float rawValue) {
        Float thresholdValue = thresholdsPerBlockPerStormSeverity.getOrDefault(stormSeverity, new HashMap<>())
                .getOrDefault(block, -99f);
        return thresholdValue > 0 && rawValue > thresholdValue;

    }

    public void put(StormSeverity stormSeverity, Map<Integer, Float> thresholds) {
        thresholdsPerBlockPerStormSeverity.computeIfAbsent(stormSeverity,
                severity -> new HashMap<>())
                .putAll(thresholds);
    }

    public StormDuration getStormDuration() {
        return stormDuration;
    }

    public List<StormSeverity> extraSeveritiesCrossed(Integer block, float rawValue, List<StormSeverity> severities) {
        return Arrays.stream(StormSeverity.values())
                .filter(stormSeverity -> !severities.contains(stormSeverity))
                .filter(stormSeverity -> thresholdCrossedAtThisSeverity(stormSeverity, block, rawValue))
                .collect(Collectors.toList());
    }

    /**
     * Returns the highest storm severity crossed by this block, as long as it's higher than the given severity
     * if it doesn't cross any higher severity, returns empty
     *
     * @param block          1km sq location
     * @param rawValue       the accumulated max for that location
     * @param severityToBeat the severity we already have
     * @return the highest of any higher severity that is crossed, or else empty
     */
    public Optional<StormSeverity> increasedSeverityThresholdCrossed(Integer block, float rawValue, Optional<StormSeverity> severityToBeat) {
        for (StormSeverity stormSeverity : StormSeverity.values()) {
            if (!severityToBeat.isPresent() || stormSeverity.moreSevere(severityToBeat.get())) {
                if (thresholdCrossedAtThisSeverity(stormSeverity, block, rawValue)) {
                    return Optional.of(stormSeverity);
                }
            }
        }
        return Optional.empty();
    }
}
