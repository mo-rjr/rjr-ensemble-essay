package uk.gov.metoffice.hello.explode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

/**
 * This is a reader for a BIL file.
 * It can be either for Float data or Byte data.
 * It uses a ByteBuffer set to Little-Endian byte-order, and reads one row into memory at a time.
 *
 * When asked to read a value, it requires the position in the file of the value to read.
 * This makes it easy to use it to read BIL files of which we only want part of the data.
 * It will throw an exception if it's asked to move backwards
 * -- it needs each subsequent request to be for a later position in the file.
 *
 * It implements Closeable (and thereby AutoCloseable) so it can be used in try-with-resources blocks
 *
 */
public class AdvancingBilFileReader<T extends Number> implements Closeable {

    // constructor parameters
    private final IOFunction<String, InputStream> inputStreamProvider;
    private final String fileReference;
    private final Function<ByteBuffer, T> valueReader;
    private final int rowInBytes;

    // class variables set during use
    private boolean opened = false;
    private BufferedInputStream bufferedInputStream;
    private ByteBuffer byteBuffer;
    private byte[] byteRow;
    private int bytesRead;
    private int currentPosition = 0;
    private T currentValue;


    public static AdvancingBilFileReader<Float> forFloats(String fileReference, int rowLength) {
        return new AdvancingBilFileReader<>(FileInputStream::new, fileReference,
                rowLength, ByteBuffer::getFloat, Float.BYTES);
    }

    public static AdvancingBilFileReader<Short> forShorts(String fileReference, int rowLength) {
        return new AdvancingBilFileReader<>(FileInputStream::new, fileReference,
                rowLength, ByteBuffer::getShort, Short.BYTES);
    }

    private AdvancingBilFileReader(IOFunction<String, InputStream> inputStreamProvider, String fileReference,
                                   int rowLength, Function<ByteBuffer, T> valueReader, int bytesPerValue) {
        this.inputStreamProvider = inputStreamProvider;
        this.valueReader = valueReader;
        this.fileReference = fileReference;
        this.rowInBytes = rowLength * bytesPerValue;
    }

    public T readNext(int requiredPosition) throws IOException {
        if (!opened) {
            open();
        }

        if (requiredPosition < currentPosition) {
            throw new IllegalArgumentException("");
        } else if (requiredPosition != currentPosition) {
            readFromByteBufferUntilRequiredPosition(requiredPosition);
        }
        return currentValue;
    }

    private void readFromByteBufferUntilRequiredPosition(int requiredPosition) throws IOException {
        while (bytesRead > 0) {
            while (byteBuffer.hasRemaining()) {
                currentValue = valueReader.apply(byteBuffer);
                currentPosition++;
                if (requiredPosition == currentPosition) {
                    return;
                }
            }
            bytesRead = bufferedInputStream.read(byteRow);
            byteBuffer.rewind();
        }
        throw new IllegalArgumentException("Could not retrieve value at " + requiredPosition +
                " of " + fileReference + ": got to end of file at position " + currentPosition);
    }

    private void open() throws FileNotFoundException, IOException {
        byteRow = new byte[rowInBytes];
        bufferedInputStream = new BufferedInputStream(inputStreamProvider.use(fileReference));
        bytesRead = bufferedInputStream.read(byteRow);
        if (bytesRead < 0) {
            throw new RuntimeException("Couldn't read any bytes from file " + fileReference);
        }

        byteBuffer = ByteBuffer.wrap(byteRow).asReadOnlyBuffer();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        currentValue = valueReader.apply(byteBuffer);
        opened = true;
    }

    @Override
    public void close() throws IOException {
        if (bufferedInputStream != null) {
            bufferedInputStream.close();
        }
    }

}
