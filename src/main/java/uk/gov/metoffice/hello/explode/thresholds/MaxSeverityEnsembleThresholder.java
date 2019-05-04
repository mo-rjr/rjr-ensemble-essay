package uk.gov.metoffice.hello.explode.thresholds;

import uk.gov.metoffice.hello.domain.Ensemble;
import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormSeverity;
import uk.gov.metoffice.hello.explode.AdvancingBilFileReader;

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
public class MaxSeverityEnsembleThresholder {

    private final AccumulationThresholdProvider accumulationThresholdProvider;

    public MaxSeverityEnsembleThresholder(AccumulationThresholdProvider accumulationThresholdProvider) {
        this.accumulationThresholdProvider = accumulationThresholdProvider;
    }

    public TreeMap<ZonedDateTime, TreeMap<Integer, StormSeverity>> calculateExceededBlocks(Ensemble ensemble,
                                                                                           StormDuration stormDuration,
                                                                                           List<Integer> sortedValidBlocks) {
        Map<ZonedDateTime, String> filesForTimeSteps = ensemble.getRunoffFilePerTimestep();
        AccumulationThresholder accumulationThresholder = accumulationThresholdProvider.getFor(stormDuration);

        // I need an ordered list of time steps
        List<ZonedDateTime> sortedTimeSteps = new ArrayList<>(filesForTimeSteps.keySet());
        Collections.sort(sortedTimeSteps);
        final int accumulationSteps = stormDuration.getTimeSteps();

        // set up collector variables
        /// this is to store the rolling accumulations as they're built up from the bil-file data
        Map<ZonedDateTime, TreeMap<Integer, Float>> accumulationsMap = sortedTimeSteps.stream()
                .collect(Collectors.toMap(zdt -> zdt, zdt -> new TreeMap<>()));
        /// this is to store the hourly output as it's created
        TreeMap<ZonedDateTime, TreeMap<Integer, StormSeverity>> hourlyStormSeverities = sortedTimeSteps.stream()
                .skip(accumulationSteps - 1) // we drop timesteps which don't have enough accumulationSteps in their sum
                .filter(TimestepUtils::onTheHour)
                .collect(Collectors.toMap(zdt -> zdt, x -> new TreeMap<>(),
                        (a, b) -> a, TreeMap::new));
        final int lastHourlyTimezoneIndex = sortedTimeSteps.indexOf(hourlyStormSeverities.lastKey());

        // cycle through all the timesteps, i.e. BIL files, that contain useful data
        for (int currentTimeIndex = 0; currentTimeIndex <= lastHourlyTimezoneIndex; currentTimeIndex++) {
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
                        if (TimestepUtils.timestepBelongsInAccumulations(thisTimeIndex, accumulationSteps, lastHourlyTimezoneIndex)) {
                            ZonedDateTime thisStepTime = sortedTimeSteps.get(thisTimeIndex);
                            Map<Integer, Float> valuesByPosition = accumulationsMap.computeIfAbsent(thisStepTime, thisTime -> new TreeMap<>());
                            Float sumSoFar = valuesByPosition.getOrDefault(block, 0.0f);
                            valuesByPosition.put(block, sumSoFar + valueForBlock);
                        }
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
                // TODO consider this, and also the FileNotFoundException
            }

            // work out whether to increase the worst storm-threshold crossed for any hourly values to which this timestep pertains
            if (TimestepUtils.timestepBelongsInAccumulations(currentTimeIndex, accumulationSteps, lastHourlyTimezoneIndex)) {
                for (ZonedDateTime hourlyTimestep : TimestepUtils.relevantHourlyTimesteps(currentZonedDateTime, hourlyStormSeverities)) {
                    TreeMap<Integer, StormSeverity> blocksWithSeverities = hourlyStormSeverities.get(hourlyTimestep);
                    for (Integer block : sortedValidBlocks) {
                        float rawValue = accumulationsMap.get(currentZonedDateTime).get(block);
                        Optional<StormSeverity> currentMaxSeverity = Optional.ofNullable(blocksWithSeverities.get(block));
                        Optional<StormSeverity> newMaxSeverity = accumulationThresholder.increasedSeverityThresholdCrossed(block, rawValue, currentMaxSeverity);
                        newMaxSeverity.ifPresent(stormSeverity -> blocksWithSeverities.put(block, stormSeverity));
                    }
                }
            }
        }

        return hourlyStormSeverities;
    }

}
