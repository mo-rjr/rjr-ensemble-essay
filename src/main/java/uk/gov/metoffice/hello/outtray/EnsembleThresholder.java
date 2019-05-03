package uk.gov.metoffice.hello.outtray;

import uk.gov.metoffice.hello.unit.AdvancingBilFileReader;
import uk.gov.metoffice.hello.message.Ensemble;
import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;
import uk.gov.metoffice.hello.unit.thresholds.AccumulationThresholdProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static uk.gov.metoffice.hello.outtray.Main.ROW_LENGTH;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class EnsembleThresholder {

    private final AccumulationThresholdProvider accumulationThresholdProvider;

    public EnsembleThresholder(AccumulationThresholdProvider accumulationThresholdProvider) {
        this.accumulationThresholdProvider = accumulationThresholdProvider;
    }

    public TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> calculateExceededBlocks(Ensemble ensemble,
                                                                     StormDuration stormDuration,
                                                                     List<Integer> sortedValidBlocks) {
        Map<ZonedDateTime, String> filesForTimeSteps = ensemble.getRunoffFilePerTimestep();

        // I need an ordered list of time steps
        List<ZonedDateTime> sortedTimeSteps = new ArrayList<>(filesForTimeSteps.keySet());
        Collections.sort(sortedTimeSteps);

        // set up variables
        Map<ZonedDateTime, Map<Integer, Float>> accumulationsMap = sortedTimeSteps.stream()
                .collect(Collectors.toMap(zdt -> zdt, zdt -> new HashMap<>()));
        final int accumulationSteps = stormDuration.getTimeSteps();
        TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> crossedThresholdsPerTimestep = new TreeMap<>();

        // cycle through all the timesteps i.e. all the BIL files
        for (int currentTimeIndex = 0; currentTimeIndex < sortedTimeSteps.size(); currentTimeIndex++) {
            ZonedDateTime currentZonedDateTime = sortedTimeSteps.get(currentTimeIndex);
            String fileName = filesForTimeSteps.get(currentZonedDateTime);
            System.out.println("File " + currentTimeIndex + ": " + currentZonedDateTime + " at fileName " + currentZonedDateTime);

            try (AdvancingBilFileReader<Float> advancingBilFileReader =
                         AdvancingBilFileReader.forFloats(fileName, ROW_LENGTH)) {

                // cycle through the required blocks
                for (Integer block : sortedValidBlocks) {

                    Float valueForBlock = advancingBilFileReader.readNext(block);

                    // add the value for this block at this timestep into all the timestep sums that need it
                    for (int futureTimeIndex = 0; futureTimeIndex < accumulationSteps; futureTimeIndex++) {
                        int thisTimeIndex = currentTimeIndex + futureTimeIndex;

                        // but no need to bother with any timesteps outside the range of the output
                        if (thisTimeIndex >= accumulationSteps - 1 && thisTimeIndex < accumulationsMap.size()) {
                            Map<Integer, Float> valuesByPosition = accumulationsMap.get(currentZonedDateTime);
                            Float sumSoFar = valuesByPosition.getOrDefault(block, 0.0f);
                            valuesByPosition.put(block, sumSoFar + valueForBlock);
                        }
                    }
                }


            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
                // TODO handle exception properly
            } catch (IOException e) {
                throw new RuntimeException(e);
                // TODO handle exception properly
            }

            // TODO turn this round so it has blocks before severity
            // now you could do something to thie timesteps's accumulated data, and then null it out
            if (currentTimeIndex >= accumulationSteps - 1) {
                Map<Integer, Float> accumulatedData = accumulationsMap.get(currentZonedDateTime);
                EnumMap<StormSeverity, List<Integer>> crossedForThisTimeStep = crossedThresholdsPerTimestep
                        .computeIfAbsent(currentZonedDateTime, x -> new EnumMap<>(StormSeverity.class));
                for (StormSeverity stormSeverity : StormSeverity.values()) {
//                    AccumulationThresholder accumulationThresholder = accumulationThresholdProvider.getFor(stormDuration, stormSeverity);
//                    List<Integer> blocks = accumulationThresholder.blocksCrossingThresholds(accumulatedData);
//                    crossedForThisTimeStep.put(stormSeverity, blocks);
                }
            }

            // at this point you could null out the timestep data
//            accumulationsMap.remove(currentZonedDateTime);
        }
        return crossedThresholdsPerTimestep;
    }

}
