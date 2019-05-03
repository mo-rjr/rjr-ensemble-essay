package uk.gov.metoffice.hello.unit.implications;

import uk.gov.metoffice.hello.message.ImpactType;
import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;
import uk.gov.metoffice.hello.unit.NewEnsembleExceedances;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ImplicationCalculator {

    private final StormImpactLevelsProvider stormImpactLevelsProvider;

    public ImplicationCalculator(StormImpactLevelsProvider stormImpactLevelsProvider) {
        this.stormImpactLevelsProvider = stormImpactLevelsProvider;
    }

    public TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<StormSeverity, EnumMap<ImpactType, Short>>>> calculateImpacts(NewEnsembleExceedances ensembleExceedances,
                                                                                                                         StormDuration stormDuration) {

        TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> allAffectedInTimeStep = findAllAffectedInTimestep(
                ensembleExceedances, stormDuration);
        TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<StormSeverity, EnumMap<ImpactType, Short>>>> result = applyImpactLevels(allAffectedInTimeStep, stormDuration);
        return result;
    }

    private TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<StormSeverity, EnumMap<ImpactType, Short>>>> applyImpactLevels(
            TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> allAffectedInTimeStep,
            StormDuration stormDuration) {

        TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<StormSeverity, EnumMap<ImpactType, Short>>>> output = new TreeMap<>();
        for (Map.Entry<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> dateTimeEntry : allAffectedInTimeStep.entrySet()) {
            ZonedDateTime zonedDateTime = dateTimeEntry.getKey();

            for (Map.Entry<Integer, List<StormSeverity>> blockEntry : dateTimeEntry.getValue().entrySet()) {
                Integer affectedBlock = blockEntry.getKey();
                StormImpactLevels stormImpactLevels = stormImpactLevelsProvider.getFor(stormDuration);
                EnumMap<StormSeverity, EnumMap<ImpactType, Short>> severityImpacts = stormImpactLevels.getValuesPerImpactType(affectedBlock);
                if (!severityImpacts.isEmpty()) {
                    output.computeIfAbsent(zonedDateTime, z -> new TreeMap<>())
                            .computeIfAbsent(affectedBlock, i -> new EnumMap<>(StormSeverity.class))
                            .putAll(severityImpacts);

                }
            }
        }
        return output;

    }


//    // TODO this one is the more recent but still not finished
//    private TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<StormSeverity, EnumMap<ImpactType, Short>>>> applyImpactLevels(Map<ZonedDateTime,
//            EnumMap<StormSeverity, List<Integer>>> allAffectedInTimeStep, StormDuration stormDuration) {
//
//        TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<StormSeverity, EnumMap<ImpactType, Short>>>> output = new TreeMap<>();
////        TreeMap<ZonedDateTime, EnumMap<StormSeverity, Map<Integer, EnumMap<ImpactType, Short>>>> output = new TreeMap<>();
//        for (Map.Entry<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> dateTimeEntry : allAffectedInTimeStep.entrySet()) {
//            ZonedDateTime zonedDateTime = dateTimeEntry.getKey();
//
//            for (Map.Entry<StormSeverity, List<Integer>> severityEntry : dateTimeEntry.getValue().entrySet()) {
////                StormSeverity stormSeverity = severityEntry.getKey();
//                StormImpactLevels stormImpactLevels = stormImpactLevelsProvider.getFor(stormDuration);
//
//                for (Integer affectedBlock : severityEntry.getValue()) {
//                    EnumMap<StormSeverity, EnumMap<ImpactType, Short>> res = stormImpactLevels.getValuesPerImpactType(affectedBlock);
//                    if (!res.isEmpty()) {
//                        output.computeIfAbsent(zonedDateTime, z -> new TreeMap<>())
//                                .computeIfAbsent(affectedBlock, i -> new EnumMap<>(StormSeverity.class))
//                                .putAll(res);
//                    }
//
//                }
//
//            }
//        }
//        return output;
//
//    }

// TODO this one is the old one
//    private TreeMap<ZonedDateTime, EnumMap<StormSeverity, Map<Integer, EnumMap<ImpactType, Short>>>> applyImpactLevels(Map<ZonedDateTime,
//            EnumMap<StormSeverity, List<Integer>>> allAffectedInTimeStep, StormDuration stormDuration) {
//
//        TreeMap<ZonedDateTime, EnumMap<StormSeverity, Map<Integer, EnumMap<ImpactType, Short>>>> output = new TreeMap<>();
//        for (Map.Entry<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> dateTimeEntry : allAffectedInTimeStep.entrySet()) {
//            ZonedDateTime zonedDateTime = dateTimeEntry.getKey();
//            for (Map.Entry<StormSeverity, List<Integer>> severityEntry : dateTimeEntry.getValue().entrySet()) {
//                StormSeverity stormSeverity = severityEntry.getKey();
//                StormImpactLevels stormImpactLevels = stormImpactLevelsProvider.getFor(stormDuration, stormSeverity);
//                Map<Integer, EnumMap<ImpactType, Short>> consequences = stormImpactLevels.getValuesPerImpactType(severityEntry.getValue());
//                EnumMap<StormSeverity, Map<Integer, EnumMap<ImpactType, Short>>> result = output.computeIfAbsent(zonedDateTime, z -> new EnumMap<>(StormSeverity.class));
//                result.put(stormSeverity, consequences);
//            }
//        }
//        return output;
//
//    }


    private TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> findAllAffectedInTimestep(NewEnsembleExceedances newEnsembleExceedances,
                                                                                                    StormDuration stormDuration) {

        TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> thresholded = newEnsembleExceedances.getThresholdsExceeded();

        List<ZonedDateTime> sortedTimes = thresholded.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        List<Integer> indexesOfHourlyValuesInList = IntStream.range(0, thresholded.size())
                .filter(i -> sortedTimes.get(i).getMinute() == 0)
                .boxed()
                .collect(Collectors.toList());

        int stepsRequiredForMax = stormDuration.getTimeSteps() + 1;

        return indexesOfHourlyValuesInList.stream()
                .collect(Collectors.toMap(sortedTimes::get,
                        index -> calculateMax(thresholded, sortedTimes, stepsRequiredForMax, index),
                        ImplicationCalculator::combine,
                        TreeMap::new));
    }

    private TreeMap<Integer, List<StormSeverity>> calculateMax(TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> thresholded,
                                                               List<ZonedDateTime> sortedTimes,
                                                               int stepsRequiredForMax, int index) {
        return IntStream.rangeClosed(index - stepsRequiredForMax + 1, index)
                .filter(i -> i >= 0)
                .mapToObj(i -> thresholded.get(sortedTimes.get(i)))
                .collect(TreeMap::new,
                        ImplicationCalculator::combine,
                        ImplicationCalculator::combine);

    }

    public static TreeMap<Integer, List<StormSeverity>> combine(TreeMap<Integer, List<StormSeverity>> first, TreeMap<Integer, List<StormSeverity>> second) {
        for (Map.Entry<Integer, List<StormSeverity>> entry : second.entrySet()) {
            first.computeIfAbsent(entry.getKey(), i -> new ArrayList<>())
                    .addAll(entry.getValue());
        }
        return first;
    }

//    private EnumMap<StormSeverity, List<Integer>> calculateMax(TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> thresholded,
//                                                               List<ZonedDateTime> sortedTimes, int stepsRequiredForMax, int index) {
//
//        return IntStream.rangeClosed(index - stepsRequiredForMax + 1, index)
//                .filter(i -> i >= 0)
//                .mapToObj(i -> thresholded.get(sortedTimes.get(i)))
//                .collect(() -> new EnumMap<>(StormSeverity.class),
//                        EnumMap::putAll,
//                        EnumMap::putAll);
//
//    }


}
