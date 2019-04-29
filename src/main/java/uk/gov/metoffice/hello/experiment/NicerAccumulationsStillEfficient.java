package uk.gov.metoffice.hello.experiment;

import uk.gov.metoffice.hello.message.Ensemble;
import uk.gov.metoffice.hello.message.StormDuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class NicerAccumulationsStillEfficient {

    private static final int ROW_LENGTH = 540;

    public Map<ZonedDateTime, Map<Integer, Float>> makeAccumulations(Ensemble ensemble,
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

        // cycle through all the timesteps i.e. all the BIL files
        for (int currentTimeIndex = 0; currentTimeIndex < sortedTimeSteps.size(); currentTimeIndex++) {
            ZonedDateTime currentZonedDateTime = sortedTimeSteps.get(currentTimeIndex);
            String fileName = filesForTimeSteps.get(currentZonedDateTime);
            System.out.println("File " + fileName + " for " + currentZonedDateTime);

            try (MaskedBilFileReader<Float> maskedBilFileReader =
                         new MaskedBilFileReader<>(ByteBuffer::getFloat, fileName,
                                 ROW_LENGTH, Float.BYTES)) {
                maskedBilFileReader.open();

                // cycle through the required blocks
                for (Integer block : sortedValidBlocks) {

                    Float valueForBlock = maskedBilFileReader.readNext(block);

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

        // now you could do something to thie timesteps's accumulated data, and then null it out
        // only do the thing for the full timesteps, but null them all
        }
        // the first, incomplete, sums are discarded, so only timesteps with n values are allowed
        // where n is the number of timesteps in the storm
        Map<ZonedDateTime, Map<Integer, Float>> neededSteps = sortedTimeSteps.stream()
                .skip(accumulationSteps - 1)
                .collect(Collectors.toMap(zdt -> zdt, accumulationsMap::get));

        return neededSteps;
    }


}
