package uk.gov.metoffice.hello.explode.impacts;

import uk.gov.metoffice.hello.domain.ImpactType;
import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormReturnPeriod;
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

        TreeMap<ZonedDateTime, TreeMap<Integer, StormReturnPeriod>> allAffectedInTimeStep = timeLocationStorms.getStorms();
        return applyImpactLevels(allAffectedInTimeStep, stormDuration);
    }

    private TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> applyImpactLevels(
            TreeMap<ZonedDateTime, TreeMap<Integer, StormReturnPeriod>> allAffectedInTimeStep,
            StormDuration stormDuration) {

        TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> output = new TreeMap<>();
        for (Map.Entry<ZonedDateTime, TreeMap<Integer, StormReturnPeriod>> dateTimeEntry : allAffectedInTimeStep.entrySet()) {
            ZonedDateTime zonedDateTime = dateTimeEntry.getKey();

            for (Map.Entry<Integer, StormReturnPeriod> blockEntry : dateTimeEntry.getValue().entrySet()) {
                Integer affectedBlock = blockEntry.getKey();
                StormReturnPeriod stormReturnPeriod = blockEntry.getValue();
                StormImpactLevels stormImpactLevels = stormImpactLevelsProvider.getFor(stormDuration);
                EnumMap<ImpactType, Short> severityImpacts = stormImpactLevels.getImpacts(affectedBlock, stormReturnPeriod);
                if (!severityImpacts.isEmpty()) {
                    output.computeIfAbsent(zonedDateTime, z -> new TreeMap<>())
                            .computeIfAbsent(affectedBlock, i -> new EnumMap<>(ImpactType.class))
                            .putAll(severityImpacts);

                }
            }
        }
        return output;

    }

    private TreeMap<Integer, List<StormReturnPeriod>> calculateMax(TreeMap<ZonedDateTime, TreeMap<Integer, List<StormReturnPeriod>>> thresholded,
                                                                   List<ZonedDateTime> sortedTimes,
                                                                   int stepsRequiredForMax, int index) {
        return IntStream.rangeClosed(index - stepsRequiredForMax + 1, index)
                .filter(i -> i >= 0)
                .mapToObj(i -> thresholded.get(sortedTimes.get(i)))
                .collect(TreeMap::new,
                        ImpactCalculator::combine,
                        ImpactCalculator::combine);

    }

    public static TreeMap<Integer, List<StormReturnPeriod>> combine(TreeMap<Integer, List<StormReturnPeriod>> first, TreeMap<Integer, List<StormReturnPeriod>> second) {
        for (Map.Entry<Integer, List<StormReturnPeriod>> entry : second.entrySet()) {
            first.computeIfAbsent(entry.getKey(), i -> new ArrayList<>())
                    .addAll(entry.getValue());
        }
        return first;
    }

}
