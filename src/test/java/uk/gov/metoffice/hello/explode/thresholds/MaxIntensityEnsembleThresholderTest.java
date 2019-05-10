package uk.gov.metoffice.hello.explode.thresholds;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.metoffice.hello.gatekeeper.EnsembleDataReader;
import uk.gov.metoffice.hello.domain.Ensemble;
import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormReturnPeriod;
import uk.gov.metoffice.hello.explode.ValidBlocksReader;
import uk.gov.metoffice.hello.domain.TimeLocationStorms;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class MaxIntensityEnsembleThresholderTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM";
//    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_SRENS_ENGWAL_201805270830_SWF_HIM";
    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
    private static final String ENSEMBLE_XML = "grids_ENS00%02d.xml";
    private static final String THRESHOLD_DATA_ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\";


    @Test
    public void makeTestForOneEnsemble() throws IOException {
        TimeLocationStorms result = calculateExceededBlocks(String.format(ENSEMBLE_XML, 5));

        try (PrintWriter printWriter = new PrintWriter("C:\\Workarea\\rjr-ensemble-essay\\src\\test\\resources\\timeLocationStorms5.json")) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(printWriter, result);
        }
    }

    private TimeLocationStorms calculateExceededBlocks(String ensembleXmlFileName) {
        // arrange
        System.out.println("*** " + ensembleXmlFileName);
        Ensemble ensemble = EnsembleDataReader.create().readFromXmlFile(ZIP_FILE_NAME, DATA_ROOT, ensembleXmlFileName);
        StormDuration stormDuration = StormDuration.ONE_HOUR;
        int expectedTimestepsInOutput = 6;
        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
        AccumulationThresholdProvider accumulationThresholdProvider = new AccumulationThresholdProvider(THRESHOLD_DATA_ROOT, validBlocks);
        MaxIntensityEnsembleThresholder testObject = new MaxIntensityEnsembleThresholder(accumulationThresholdProvider);

        // act
        TreeMap<ZonedDateTime, TreeMap<Integer, StormReturnPeriod>> result = testObject.calculateExceededBlocks(ensemble,
                stormDuration, validBlocks);

        // assert
        assertNotNull(result);
        assertEquals(expectedTimestepsInOutput, result.size());

        return new TimeLocationStorms(ensembleXmlFileName, result);
    }
}