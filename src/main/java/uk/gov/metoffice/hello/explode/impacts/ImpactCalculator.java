package uk.gov.metoffice.hello.explode.impacts;

import uk.gov.metoffice.hello.domain.ImpactType;
import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormSeverity;
import uk.gov.metoffice.hello.domain.TimeLocationStorms;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ImpactCalculator {

    private final StormImpactLevelsProvider stormImpactLevelsProvider;

    public ImpactCalculator(StormImpactLevelsProvider stormImpactLevelsProvider) {
        this.stormImpactLevelsProvider = stormImpactLevelsProvider;
    }

    public TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> calculateImpacts(TimeLocationStorms timeLocationStorms,
                                                                                                                         StormDuration stormDuration) {

        TreeMap<ZonedDateTime, TreeMap<Integer, StormSeverity>> allAffectedInTimeStep = timeLocationStorms.getStorms();
        return applyImpactLevels(allAffectedInTimeStep, stormDuration);
    }

    private TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> applyImpactLevels(
            TreeMap<ZonedDateTime, TreeMap<Integer, StormSeverity>> allAffectedInTimeStep,
            StormDuration stormDuration) {

        TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> output = new TreeMap<>();
        for (Map.Entry<ZonedDateTime, TreeMap<Integer, StormSeverity>> dateTimeEntry : allAffectedInTimeStep.entrySet()) {
            ZonedDateTime zonedDateTime = dateTimeEntry.getKey();

            for (Map.Entry<Integer, StormSeverity> blockEntry : dateTimeEntry.getValue().entrySet()) {
                Integer affectedBlock = blockEntry.getKey();
                StormSeverity stormSeverity = blockEntry.getValue();
                StormImpactLevels stormImpactLevels = stormImpactLevelsProvider.getFor(stormDuration);
                EnumMap<ImpactType, Short> severityImpacts = stormImpactLevels.getImpacts(affectedBlock, stormSeverity);
                if (!severityImpacts.isEmpty()) {
                    output.computeIfAbsent(zonedDateTime, z -> new TreeMap<>())
                            .computeIfAbsent(affectedBlock, i -> new EnumMap<>(ImpactType.class))
                            .putAll(severityImpacts);

                }
            }
        }
        return output;

    }

    private TreeMap<Integer, List<StormSeverity>> calculateMax(TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> thresholded,
                                                               List<ZonedDateTime> sortedTimes,
                                                               int stepsRequiredForMax, int index) {
        return IntStream.rangeClosed(index - stepsRequiredForMax + 1, index)
                .filter(i -> i >= 0)
                .mapToObj(i -> thresholded.get(sortedTimes.get(i)))
                .collect(TreeMap::new,
                        ImpactCalculator::combine,
                        ImpactCalculator::combine);

    }

    public static TreeMap<Integer, List<StormSeverity>> combine(TreeMap<Integer, List<StormSeverity>> first, TreeMap<Integer, List<StormSeverity>> second) {
        for (Map.Entry<Integer, List<StormSeverity>> entry : second.entrySet()) {
            first.computeIfAbsent(entry.getKey(), i -> new ArrayList<>())
                    .addAll(entry.getValue());
        }
        return first;
    }

}
