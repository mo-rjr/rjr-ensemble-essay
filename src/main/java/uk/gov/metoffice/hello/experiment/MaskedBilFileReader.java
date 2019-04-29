package uk.gov.metoffice.hello.experiment;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class MaskedBilFileReader<T extends Number> implements AutoCloseable {

    private final Function<ByteBuffer, T> valueReader;
    private final String fileReference;

    private final int rowInBytes;

    private BufferedInputStream bufferedInputStream;
    private ByteBuffer byteBuffer;
    private byte[] byteRow;
    private int bytesRead;
    private int currentPosition = 0;
    private T currentValue;

    public MaskedBilFileReader(Function<ByteBuffer, T> valueReader, String fileReference, int rowLength, int bytesPerValue) {
        this.valueReader = valueReader;
        this.fileReference = fileReference;
        this.rowInBytes = rowLength * bytesPerValue;
    }

    public void open() throws FileNotFoundException, IOException {
        byteRow = new byte[rowInBytes];
        bufferedInputStream = new BufferedInputStream(new FileInputStream(fileReference));
        bytesRead = bufferedInputStream.read(byteRow);
        if (bytesRead < 0) {
            throw new RuntimeException("Couldn't read any bytes from file " + fileReference);
        }

        byteBuffer = ByteBuffer.wrap(byteRow).asReadOnlyBuffer();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        currentValue = valueReader.apply(byteBuffer);
    }

    public T readNext(int requiredPosition) throws IOException {

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
                ": got to end of file at position " + currentPosition);
    }

    @Override
    public void close() {
        if (bufferedInputStream != null) {
            try {
                bufferedInputStream.close();
            } catch (IOException e) {
                // TODO is it OK to swallow this exception?
            }
        }
    }
}
