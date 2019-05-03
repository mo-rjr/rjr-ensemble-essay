package uk.gov.metoffice.hello.unit.thresholds;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.metoffice.hello.gatekeeper.EnsembleDataReader;
import uk.gov.metoffice.hello.message.Ensemble;
import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;
import uk.gov.metoffice.hello.outtray.ValidBlocksReader;
import uk.gov.metoffice.hello.unit.NewEnsembleExceedances;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertNotNull;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class NewEnsembleThresholderTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM";
//    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_SRENS_ENGWAL_201805270830_SWF_HIM";
    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
    private static final String ENSEMBLE_XML = "grids_ENS00%02d.xml";
    private static final String THRESHOLD_DATA_ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\";


    @Test
    public void makeTestForAllEnsembles() throws IOException {
        List<NewEnsembleExceedances> newEnsembleExceedances = IntStream.range(0, 24)
                .mapToObj(i -> String.format(ENSEMBLE_XML, i))
                .map(this::calculateExceededBlocks)
                .collect(Collectors.toList());

        try (PrintWriter printWriter = new PrintWriter("C:\\Workarea\\rjr-ensemble-essay\\src\\test\\resources\\ensembleThresholderTestOutput.json")) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(printWriter, newEnsembleExceedances);
        }
    }

    @Test
    public void makeTestForOneEnsemble() throws IOException {
        NewEnsembleExceedances result = calculateExceededBlocks(String.format(ENSEMBLE_XML, 5));
        try (PrintWriter printWriter = new PrintWriter("C:\\Workarea\\rjr-ensemble-essay\\src\\test\\resources\\newEnsembleExceedances5.json")) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(printWriter, result);
        }
    }

    private NewEnsembleExceedances calculateExceededBlocks(String ensembleXmlFileName) {
        // arrange
        System.out.println("*** " + ensembleXmlFileName);
        Ensemble ensemble = EnsembleDataReader.create().readFromXmlFile(ZIP_FILE_NAME, DATA_ROOT, ensembleXmlFileName);
        StormDuration stormDuration = StormDuration.ONE_HOUR;
        int expectedTimestepsInOutput = ensemble.getRunoffFilePerTimestep().size() - stormDuration.getTimeSteps() + 1;
        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
        AccumulationThresholdProvider accumulationThresholdProvider = new AccumulationThresholdProvider(THRESHOLD_DATA_ROOT, validBlocks);
        NewEnsembleThresholder testObject = new NewEnsembleThresholder(accumulationThresholdProvider);

        // act
        TreeMap<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> result = testObject.calculateExceededBlocks(ensemble,
                stormDuration, validBlocks);

        // assert
        assertNotNull(result);

        NewEnsembleExceedances ensembleExceedances = new NewEnsembleExceedances(ensembleXmlFileName, result);
        System.out.println(ensembleExceedances);
//        validate(ensembleExceedances, expectedTimestepsInOutput);
        return ensembleExceedances;
    }

//    private void validate(NewEnsembleExceedances newEnsembleExceedances, int expectedTimestepsInOutput) {
//        assertEquals(expectedTimestepsInOutput, newEnsembleExceedances.getThresholdsExceeded().size());
//        assertHigherSeveritySubsetOfLower(newEnsembleExceedances, StormSeverity.ONE_THOUSAND_YEARS, StormSeverity.ONE_HUNDRED_YEARS);
//        assertHigherSeveritySubsetOfLower(newEnsembleExceedances, StormSeverity.ONE_HUNDRED_YEARS, StormSeverity.THIRTY_YEARS);
//    }
//
//    private void assertHigherSeveritySubsetOfLower(NewEnsembleExceedances newEnsembleExceedances, StormSeverity higher, StormSeverity lower) {
//        for ( Map.Entry<ZonedDateTime, TreeMap<Integer, List<StormSeverity>>> entry : newEnsembleExceedances.getThresholdsExceeded().entrySet()) {
//            EnumMap<StormSeverity, List<Integer>> exceededMap = entry.getValue();
//            List<Integer> lowerThresholds = exceededMap.get(lower);
//            List<Integer> higherThresholds = exceededMap.get(higher);
//            assertFalse(lowerThresholds == null && higherThresholds != null);
//            if (lowerThresholds != null && higherThresholds != null) {
//                assertTrue(lowerThresholds.containsAll(higherThresholds));
//            }
//        }
//    }
}