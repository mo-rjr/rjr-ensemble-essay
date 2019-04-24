package uk.gov.metoffice.hello.unit;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ReadValuesForBlocks {

    public Map<Integer, Float> fromBlocksFromFile(String fileName, List<Integer> areaBlocks) {

        try {
            Path path = Paths.get(fileName);
            byte[] byteArray = Files.readAllBytes(path);
            System.out.println(byteArray.length);
            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            float value;
            int position = 0;
            Map<Integer, Float> valuesForBlocks = new HashMap<>();
            while (byteBuffer.hasRemaining() && valuesForBlocks.size() < areaBlocks.size()) {
                value = byteBuffer.getFloat();
                if (areaBlocks.contains(position)) {
                    valuesForBlocks.put(position, value);
                }
                position++;
            }
            return valuesForBlocks;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}
