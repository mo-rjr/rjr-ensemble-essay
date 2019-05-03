package uk.gov.metoffice.hello.outtray;

import uk.gov.metoffice.hello.message.Ensemble;
import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;
import uk.gov.metoffice.hello.unit.AdvancingBilFileReader;
import uk.gov.metoffice.hello.unit.thresholds.AccumulationThresholdProvider;
import uk.gov.metoffice.hello.unit.thresholds.AccumulationThresholder;

import java.io.IOException;
import java.io.UncheckedIOException;
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
public class NewEnsembleThresholder {

    private final AccumulationThresholdProvider accumulationThresholdProvider;

    public NewEnsembleThresholder(AccumulationThresholdProvider accumulationThresholdProvider) {
        this.accumulationThresholdProvider = accumulationThresholdProvider;
    }

    public TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> calculateExceededBlocks(Ensemble ensemble,
                                                                                                 StormDuration stormDuration,
                                                                                                 List<Integer> sortedValidBlocks) {
        Map<ZonedDateTime, String> filesForTimeSteps = ensemble.getRunoffFilePerTimestep();
        AccumulationThresholder accumulationThresholder = accumulationThresholdProvider.getFor(stormDuration);

        // I need an ordered list of time steps
        List<ZonedDateTime> sortedTimeSteps = new ArrayList<>(filesForTimeSteps.keySet());
        Collections.sort(sortedTimeSteps);
        final int accumulationSteps = stormDuration.getTimeSteps();

        // set up variables
        TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> crossedThresholdsPerTimestep = new TreeMap<>();
        Map<ZonedDateTime, TreeMap<Integer, Float>> accumulationsMap = sortedTimeSteps.stream()
                .collect(Collectors.toMap(zdt -> zdt, zdt -> new TreeMap<>()));

        // cycle through all the timesteps i.e. all the BIL files
        for (int currentTimeIndex = 0; currentTimeIndex < sortedTimeSteps.size(); currentTimeIndex++) {
            ZonedDateTime currentZonedDateTime = sortedTimeSteps.get(currentTimeIndex);
            String fileName = filesForTimeSteps.get(currentZonedDateTime);
            System.out.println("File " + currentTimeIndex + ": " + currentZonedDateTime + " at fileName " + currentZonedDateTime);

            try (AdvancingBilFileReader<Float> bilFileReader = AdvancingBilFileReader.forFloats(fileName, ROW_LENGTH)) {

                // cycle through the required blocks
                for (Integer block : sortedValidBlocks) {

                    Float valueForBlock = bilFileReader.readNext(block);

                    // add the value for this block at this timestep into all the timestep sums that need it
                    for (int futureTimeIndex = 0; futureTimeIndex < accumulationSteps; futureTimeIndex++) {
                        int thisTimeIndex = currentTimeIndex + futureTimeIndex;

                        // but no need to bother with any timesteps outside the range of the output
                        if (thisTimeIndex >= accumulationSteps - 1 && thisTimeIndex < accumulationsMap.size()) {
                            ZonedDateTime thisStepTime = sortedTimeSteps.get(thisTimeIndex);
                            Map<Integer, Float> valuesByPosition = accumulationsMap.computeIfAbsent(thisStepTime,
                                    thisTime -> new TreeMap<>());
                            Float sumSoFar = valuesByPosition.getOrDefault(block, 0.0f);
                            valuesByPosition.put(block, sumSoFar + valueForBlock);
                        }
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
                // TODO consider this, and also the FileNotFoundException
            }

            // now threshold the data for this timestep
            // because there will be no future data to add to its summation
            if (currentTimeIndex >= accumulationSteps - 1) {
                TreeMap<Integer, Float> accumulatedData = accumulationsMap.get(currentZonedDateTime);
                TreeMap<Integer, List<StormSeverity>> exceedancesForThisTimeStep = accumulationThresholder
                        .exceededThresholds(accumulatedData);
                crossedThresholdsPerTimestep.put(currentZonedDateTime, exceedancesForThisTimeStep);
            }
        }

        return crossedThresholdsPerTimestep;
    }
}
