package uk.gov.metoffice.hello.explode.thresholds;

import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormReturnPeriod;

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

    private final EnumMap<StormReturnPeriod, Map<Integer, Float>> thresholdsPerBlockPerStormSeverity = new EnumMap<>(StormReturnPeriod.class);

    public AccumulationThresholder(StormDuration stormDuration) {
        this.stormDuration = stormDuration;
    }

    public TreeMap<Integer, List<StormReturnPeriod>> exceededThresholds(TreeMap<Integer, Float> accumulatedData) {
        Map<Integer, List<StormReturnPeriod>> exceededThresholds = accumulatedData.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> stormSeveritiesForBlock(entry.getKey(), entry.getValue())));
        // now get rid of any blocks with an empty list of exceeded thresholds
        return exceededThresholds.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new));
    }

    public List<StormReturnPeriod> stormSeveritiesForBlock(Integer block, Float rawValue) {
        return Arrays.stream(StormReturnPeriod.values())
                .filter(severity -> thresholdCrossedAtThisSeverity(severity, block, rawValue))
                .collect(Collectors.toList());
    }

    private boolean thresholdCrossedAtThisSeverity(StormReturnPeriod stormReturnPeriod, Integer block, Float rawValue) {
        Float thresholdValue = thresholdsPerBlockPerStormSeverity.getOrDefault(stormReturnPeriod, new HashMap<>())
                .getOrDefault(block, -99f);
        return thresholdValue > 0 && rawValue > thresholdValue;

    }

    public void put(StormReturnPeriod stormReturnPeriod, Map<Integer, Float> thresholds) {
        thresholdsPerBlockPerStormSeverity.computeIfAbsent(stormReturnPeriod,
                severity -> new HashMap<>())
                .putAll(thresholds);
    }

    public StormDuration getStormDuration() {
        return stormDuration;
    }

    public List<StormReturnPeriod> extraSeveritiesCrossed(Integer block, float rawValue, List<StormReturnPeriod> severities) {
        return Arrays.stream(StormReturnPeriod.values())
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
    public Optional<StormReturnPeriod> increasedSeverityThresholdCrossed(Integer block, float rawValue,
                                                                         Optional<StormReturnPeriod> severityToBeat) {
        for (StormReturnPeriod stormReturnPeriod : StormReturnPeriod.values()) {
            if (!severityToBeat.isPresent() || stormReturnPeriod.moreSevere(severityToBeat.get())) {
                if (thresholdCrossedAtThisSeverity(stormReturnPeriod, block, rawValue)) {
                    return Optional.of(stormReturnPeriod);
                }
            }
        }
        return Optional.empty();
    }
}
