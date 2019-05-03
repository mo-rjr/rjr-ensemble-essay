package uk.gov.metoffice.hello.experiment;

import org.junit.Test;
import uk.gov.metoffice.hello.gatekeeper.EnsembleDataReader;
import uk.gov.metoffice.hello.message.Ensemble;
import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.outtray.NicerAccumulationsStillEfficient;
import uk.gov.metoffice.hello.outtray.ValidBlocksReader;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class NicerAccumulationsStillEfficientTest {

    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM";
    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
    private static final String ENSEMBLE_XML = "grids_ENS0013.xml";

    @Test
    public void makeAccumulations() {
        // arrange
        NicerAccumulationsStillEfficient testObject = new NicerAccumulationsStillEfficient();
        Ensemble ensemble = EnsembleDataReader.create().readFromXmlFile(ZIP_FILE_NAME, DATA_ROOT, ENSEMBLE_XML);
        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();

        // act
        Map<ZonedDateTime, Map<Integer, Float>> result = testObject.makeAccumulations(ensemble, StormDuration.ONE_HOUR, validBlocks);

        // assert
        assertEquals(26, result.size());
        int testIndex = 0;
        for (Map.Entry<ZonedDateTime, Map<Integer, Float>> entry: result.entrySet()) {
            assertEquals(157125, entry.getValue().size());
            System.out.println("For ZonedDateTime " + entry.getKey() + " at index " + testIndex + " there are "
                    + entry.getValue().size() + " entries");
            testIndex++;
        }
    }
}