package uk.gov.metoffice.hello.outtray;

import uk.gov.metoffice.hello.domain.Ensemble;
import uk.gov.metoffice.hello.domain.StormDuration;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The aim of this class is to have as little surface runoff data in memory at once as possible
 * It reads the files one at a time, putting valid data into all the accumulations where it will be needed
 * Once it has read enough for the an accumulated timestep to be finished, it saves that timestep as a file
 */
// TODO fill in Javadoc
public class DoAccumulationsEfficiently {

    private static final int ROW_BYTES = 540 * 4;

    public Map<ZonedDateTime, Map<Integer, Float>> makeAccumulations(Ensemble ensemble, StormDuration stormDuration,
                                                                     List<Integer> sortedValidBlocks) {
        Map<ZonedDateTime, String> filesForTimeSteps = ensemble.getRunoffFilePerTimestep();

        // I need an ordered list of time steps
        List<ZonedDateTime> sortedTimeSteps = new ArrayList<>(filesForTimeSteps.keySet());
        Collections.sort(sortedTimeSteps);

        // set up variables
        Map<ZonedDateTime, Map<Integer, Float>> accumulationsMap = sortedTimeSteps.stream()
                .collect(Collectors.toMap(zdt -> zdt, zdt -> new HashMap<>()));
        final int accumulationSteps = stormDuration.getTimeSteps();


        for (int currentTimeIndex = 0; currentTimeIndex < sortedTimeSteps.size(); currentTimeIndex++) {
            ZonedDateTime currentZonedDateTime = sortedTimeSteps.get(currentTimeIndex);
            String fileName = filesForTimeSteps.get(currentZonedDateTime);
            System.out.println("File " + fileName + " for " + currentZonedDateTime);
            int indexOfNextValidBlock = 0;
            try (BufferedInputStream inputStream = new BufferedInputStream(
                    new FileInputStream(fileName))) {

                byte[] oneRow = new byte[ROW_BYTES];
                ByteBuffer byteBuffer = null;
                int position = 0;

                int bytesRead = inputStream.read(oneRow);
                if (bytesRead < 0) {
                    throw new RuntimeException("Couldn't read any bytes from file for " + currentZonedDateTime);
                }
                byteBuffer = ByteBuffer.wrap(oneRow).asReadOnlyBuffer();
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                Float value = null;
                while (bytesRead > 0 && indexOfNextValidBlock < sortedValidBlocks.size()) {
                    while (byteBuffer.hasRemaining() && indexOfNextValidBlock < sortedValidBlocks.size()) {
                        value = byteBuffer.getFloat();
                        if (position == sortedValidBlocks.get(indexOfNextValidBlock)) {
                            indexOfNextValidBlock++;

                            for (int futureTimeIndex = 0; futureTimeIndex < accumulationSteps; futureTimeIndex++) {
                                int thisTimeIndex = currentTimeIndex + futureTimeIndex;

                                if (thisTimeIndex >= accumulationSteps - 1 && thisTimeIndex < accumulationsMap.size()) {
                                    Map<Integer, Float> valuesByPosition = accumulationsMap.get(currentZonedDateTime);
                                    Float sumSoFar = valuesByPosition.getOrDefault(position, 0.0f);
                                    valuesByPosition.put(position, sumSoFar + value);
                                }
                            }

                            // delete later
                            if (value < 0.0) {
                                System.out.println("For " + currentZonedDateTime + " position " + position + " has value " + value);
                            }
                        }
                        position++;
                    }

                    bytesRead = inputStream.read(oneRow);
                    byteBuffer.rewind();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
                // TODO handle exception properly
            }
        }

//        for (int removeIndex = 0; removeIndex < (accumulationSteps - 1); removeIndex++) {
//            accumulationsMap.remove(sortedTimeSteps.get(removeIndex));
//        }
//        return accumulationsMap;

        Map<ZonedDateTime, Map<Integer, Float>> neededSteps = sortedTimeSteps.stream()
                .skip(accumulationSteps - 1)
                .collect(Collectors.toMap(zdt -> zdt, accumulationsMap::get));

        return neededSteps;
    }
}
