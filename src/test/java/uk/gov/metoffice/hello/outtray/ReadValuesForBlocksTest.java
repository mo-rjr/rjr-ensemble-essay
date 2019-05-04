package uk.gov.metoffice.hello.outtray;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.metoffice.hello.gatekeeper.AdminAreaReader;
import uk.gov.metoffice.hello.domain.AdminArea;
import uk.gov.metoffice.hello.outtray.ReadValuesForBlocks;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ReadValuesForBlocksTest {

    @Test
    public void readThresholds() {
        // arrange
        ReadValuesForBlocks<Float> testObject = new ReadValuesForBlocks<>(ByteBuffer::getFloat,
                Float.BYTES, 540);
        String fileName = "U:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\" +
                "p30_1hr_a.bil";
        AdminAreaReader adminAreaReader = new AdminAreaReader();
        AdminArea adminArea = adminAreaReader.read("C:\\Workarea\\rjr-ensemble-essay\\src\\main\\resources\\adminAreas.json").get(65);



        // act
        Map<Integer, Float> thresholdValues = testObject.blockValuesFromFile(fileName, adminArea.getBlocks());

        // assert
        Assert.assertNotNull(thresholdValues);



    }
}