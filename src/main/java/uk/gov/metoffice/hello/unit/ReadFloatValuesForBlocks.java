package uk.gov.metoffice.hello.unit;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ReadFloatValuesForBlocks {

    private static final int BYTES_IN_FLOAT = 4;

    private final InputStreamProvider inputStreamProvider;

    private final int rowLength;

    public ReadFloatValuesForBlocks(InputStreamProvider inputStreamProvider, int rowLength) {
        this.inputStreamProvider = inputStreamProvider;
        this.rowLength = rowLength;
    }

    public Map<Integer, Float> blockValuesFromFile(String fileName, List<Integer> areaBlocks) {

        try (BufferedInputStream inputStream = new BufferedInputStream(inputStreamProvider.open(fileName))) {
//            Path path = Paths.get(fileName);
//            byte[] byteArray = Files.readAllBytes(path);
//            System.out.println(byteArray.length);
            byte[] oneRow = new byte[rowLength * BYTES_IN_FLOAT];

            ByteBuffer byteBuffer = null;

            int position = 0;

            int bytesRead = inputStream.read(oneRow);
            if (bytesRead < 0) {
                throw new RuntimeException("Couldn't read any bytes from file " + fileName);
            }

            byteBuffer = ByteBuffer.wrap(oneRow).asReadOnlyBuffer();
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            float floatValue = 0.0f;

            Map<Integer, Float> valuesForBlocks = new HashMap<>();

            while (bytesRead > 0 && valuesForBlocks.size() < areaBlocks.size()) {
                while (byteBuffer.hasRemaining() && valuesForBlocks.size() < areaBlocks.size()) {
                    floatValue = byteBuffer.getFloat();
                    if (areaBlocks.contains(position)) {
                        valuesForBlocks.put(position, floatValue);

                    }
                    position++;
                }

                bytesRead = inputStream.read(oneRow);
                byteBuffer.rewind();
            }
            return valuesForBlocks;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}
