package uk.gov.metoffice.hello.outtray;

import uk.gov.metoffice.hello.message.OneDurationOneEnsembleOneArea;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class Accumulator {

    private final ReadValuesForBlocks<Float> readFloatValuesForBlocks;

    public Accumulator(ReadValuesForBlocks<Float> readFloatValuesForBlocks) {
        this.readFloatValuesForBlocks = readFloatValuesForBlocks;
    }


    public Map<ZonedDateTime, Map<Integer, Float>> accumulateValues(OneDurationOneEnsembleOneArea spec) {
        List<Integer> blocks = spec.getAdminArea().getBlocks();
        Map<ZonedDateTime, String> filesForTimeSteps = spec.getEnsemble().getRunoffFilePerTimestep();


        Map<ZonedDateTime, Map<Integer, Float>> rawValues = filesForTimeSteps.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> readFloatValuesForBlocks.blockValuesFromFile(e.getValue(), blocks)));


        List<ZonedDateTime> timeSteps = new ArrayList<>(rawValues.keySet());
        Collections.sort(timeSteps);

        int timespan = spec.getStormDuration().getTimeSteps();

//        Map<Integer, Map<Integer, Float>> blockToTimeIndexToSummedValue = new HashMap<>();
        Map<Integer, Map<Integer, Float>> timeIndexToBlockToSummedValue = new HashMap<>();


        for (Integer block : blocks) {
            for (int index = 0; index < timeSteps.size(); index++) {
                float rawValue = rawValues.get(timeSteps.get(index)).get(block);
                for (int spanSteps = 0; spanSteps < timespan; spanSteps++) {
                    int affectedTimeIndex = index + spanSteps;

                    Map<Integer, Float> blockToRunningSumMap = timeIndexToBlockToSummedValue.computeIfAbsent(affectedTimeIndex,
                            x -> new HashMap<>());

                    float valueSoFar = blockToRunningSumMap.getOrDefault(block, 0.0f);
                    blockToRunningSumMap.put(block, valueSoFar + rawValue);
//                    Map<Integer, Float> blockMap = blockToTimeIndexToSummedValue.computeIfAbsent(block, x -> new HashMap<>());
//                    float valueSoFar = blockMap.getOrDefault(position, 0.0f);
//                    blockMap.put(position, valueSoFar + rawValue);
                }
            }
        }

        Map<ZonedDateTime, Map<Integer, Float>> accumulatedTimeSteps = new HashMap<>();
        for (int step = timespan - 1; step < timeSteps.size(); step++) {
            accumulatedTimeSteps.put(timeSteps.get(step), timeIndexToBlockToSummedValue.get(step));
        }
        return accumulatedTimeSteps;

    }
}
