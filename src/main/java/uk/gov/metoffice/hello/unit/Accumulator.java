package uk.gov.metoffice.hello.unit;

import uk.gov.metoffice.hello.message.OneDurationOneEnsembleOneArea;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class Accumulator {

    private final ReadValuesForBlocks readValuesForBlocks;

    public Accumulator(ReadValuesForBlocks readValuesForBlocks) {
        this.readValuesForBlocks = readValuesForBlocks;
    }


    public Map<ZonedDateTime, Map<Integer, Float>> accumulateValues(OneDurationOneEnsembleOneArea spec) {
        List<Integer> blocks = spec.getAdminArea().getSquares();
        Map<ZonedDateTime, String> filesForTimeSteps = spec.getEnsemble().getRunoffFileForTimestep();


        Map<ZonedDateTime, Map<Integer, Float>> rawValues = filesForTimeSteps.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> readValuesForBlocks.fromBlocksFromFile(e.getValue(), blocks)));


        List<ZonedDateTime> timeSteps = new ArrayList<>(rawValues.keySet());
        int timespan = spec.getDuration().getTimeSteps();

        Map<Integer, Map<Integer, Float>> blockToTimeIndexToSummedValue = new HashMap<>();


        for (Integer block : blocks) {
            for (int index = 0; index < timeSteps.size(); index++) {
                float rawValue = rawValues.get(timeSteps.get(index)).get(block);
                for (int spanSteps = 0; spanSteps < timespan; spanSteps++) {
                    int position = index + spanSteps;
                    Map<Integer, Float> blockMap = blockToTimeIndexToSummedValue.computeIfAbsent(block, x -> new HashMap<>());
                    float valueSoFar = blockMap.getOrDefault(position, 0.0f);
                    blockMap.put(position, valueSoFar + rawValue);
                }
            }
        }

        Map<ZonedDateTime, Map<Integer, Float>> accumulatedTimeSteps = new HashMap<>();
        for (int step = timespan - 1; step < timeSteps.size(); step++) {
            accumulatedTimeSteps.put(timeSteps.get(step), blockToTimeIndexToSummedValue.get(step));
        }
        return accumulatedTimeSteps;

    }
}
