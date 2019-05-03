package uk.gov.metoffice.hello.experiment;

import org.junit.Test;
import uk.gov.metoffice.hello.outtray.ValidBlocksReader;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ValidBlocksReaderTest {

    @Test
    public void readValidBlocks() {
        // arrange
        ValidBlocksReader testObject = new ValidBlocksReader();

        // act
        List<Integer> validBlocks = testObject.readValidBlocks();

        // assert
        assertEquals(157_125, validBlocks.size());

    }


}