package uk.gov.metoffice.hello.unit;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ReadValuesForBlocks<T extends Number> {

    private final Function<ByteBuffer, T> valueReader;

    private final int numberOfBytesPerValue;

    private final int rowLength;

    public ReadValuesForBlocks(Function<ByteBuffer, T> valueReader,
                               int numberOfBytesPerValue,
                               int rowLength) {
        this.valueReader = valueReader;
        this.numberOfBytesPerValue = numberOfBytesPerValue;
        this.rowLength = rowLength;
    }

    public Map<Integer, T> blockValuesFromFile(String fileReference, List<Integer> blocks) {
        try (BufferedInputStream inputStream = new BufferedInputStream(
                new FileInputStream(fileReference))) {

            byte[] oneRow = new byte[rowLength * numberOfBytesPerValue];

            ByteBuffer byteBuffer = null;

            int position = 0;

            int bytesRead = inputStream.read(oneRow);
            if (bytesRead < 0) {
                throw new RuntimeException("Couldn't read any bytes from file " + fileReference);
            }

            byteBuffer = ByteBuffer.wrap(oneRow).asReadOnlyBuffer();
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            T value = null;

            Map<Integer, T> valuesForBlocks = new HashMap<>();

            while (bytesRead > 0 && valuesForBlocks.size() < blocks.size()) {
                while (byteBuffer.hasRemaining() && valuesForBlocks.size() < blocks.size()) {
                    value = valueReader.apply(byteBuffer);
                    if (blocks.contains(position)) {
                        valuesForBlocks.put(position, value);
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
