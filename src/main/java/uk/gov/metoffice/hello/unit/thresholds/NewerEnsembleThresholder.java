package uk.gov.metoffice.hello.unit.thresholds;

import uk.gov.metoffice.hello.message.Ensemble;
import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;
import uk.gov.metoffice.hello.unit.AdvancingBilFileReader;
import uk.gov.metoffice.hello.unit.implications.ImplicationCalculator;

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
public class NewerEnsembleThresholder {

    private final AccumulationThresholdProvider accumulationThresholdProvider;

    public NewerEnsembleThresholder(AccumulationThresholdProvider accumulationThresholdProvider) {
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

        // set up collector variables
//        TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> crossedThresholdsPerTimestep = new TreeMap<>();
        /// this is to store the rolling accumulations as they're set up
        Map<ZonedDateTime, TreeMap<Integer, Float>> accumulationsMap = sortedTimeSteps.stream()
                .collect(Collectors.toMap(zdt -> zdt, zdt -> new TreeMap<>()));
        /// this is to store the hourly output as it's created
        TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> hourlyCrossedThresholds = sortedTimeSteps.stream()
                .skip(accumulationSteps - 1) // we drop timesteps which don't have enough accumulationSteps in their sum
                .filter(this::onTheHour)
                .collect(Collectors.toMap(zdt -> zdt,
                        zdt -> new TreeMap<>(),
                        ImplicationCalculator::combine,
                        TreeMap::new));
        int lastHourlyTimezoneIndex = sortedTimeSteps.indexOf(hourlyCrossedThresholds.lastKey());

        // cycle through all the timesteps i.e. all the BIL files, that contain useful data (drop odd ones at end)
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
                        if (timestepBelongsInAccumulations(thisTimeIndex, accumulationSteps, sortedTimeSteps.size())) {
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

            // threshold for any hourly values to which this timestep pertains
            for (ZonedDateTime hourlyTimestep : relevantHourlyTimesteps(currentZonedDateTime, hourlyCrossedThresholds)) {
                TreeMap<Integer, List<StormSeverity>> blocksWithSeverities = hourlyCrossedThresholds.computeIfAbsent(hourlyTimestep,
                        z -> new TreeMap<>());
                for (Integer block : sortedValidBlocks) {
                    float rawValue = accumulationsMap.get(hourlyTimestep)
                            .get(block);
                    List<StormSeverity> currentSeveritiesCrossed = blocksWithSeverities.getOrDefault(block, new ArrayList<>());
                    List<StormSeverity> extraSeveritiesCrossed = accumulationThresholder.extraSeveritiesCrossed(block, rawValue, currentSeveritiesCrossed);
                    if (!extraSeveritiesCrossed.isEmpty()) {
                        blocksWithSeverities.computeIfAbsent(block, i -> new ArrayList<>())
                                .addAll(extraSeveritiesCrossed);

                    }
                }
            }
        }

        return hourlyCrossedThresholds;
    }

    private boolean timestepBelongsInAccumulations(int thisTimestepIndex, int accumulationSteps, int totalTimesteps) {
        return thisTimestepIndex >= accumulationSteps - 1 && thisTimestepIndex < totalTimesteps;
    }

    private boolean onTheHour(ZonedDateTime zonedDateTime) {
        return zonedDateTime.getMinute() == 0;
    }

    /**
     * The past hour for purposes of thresholding includes five values not four,
     * e.g. the 6pm value is over 5pm, 5.15, 5.30, 5.45, 6pm
     * not just 5.15, 5.30, 5.45, 6pm
     * @param currentZonedDateTime this current timestep, e.g. 5pm or 5.15
     * @param hourlyCrossedThresholds the accumulating treemap, where its keys are only the hourly timesteps, e.g. 5pm, 6pm
     * @return all hourly timesteps to which this pertains
     * e.g. 5pm pertains to 5pm and 6pm,
     *      5.15 pertains to 6pm
     *      and if the data only goes out to 8.45 then 8.30 pertains to no hourly timesteps
     */
    private List<ZonedDateTime> relevantHourlyTimesteps(ZonedDateTime currentZonedDateTime,
                                                        TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> hourlyCrossedThresholds) {
        List<ZonedDateTime> relevantHourlyTimesteps = new ArrayList<>();
        if (onTheHour(currentZonedDateTime)) {
            relevantHourlyTimesteps.add(currentZonedDateTime);
        }

        ZonedDateTime nextHourlyValue = hourlyCrossedThresholds.higherKey(currentZonedDateTime);
        if (nextHourlyValue != null) {
            relevantHourlyTimesteps.add(nextHourlyValue);
        }
        return relevantHourlyTimesteps;
    }


}
