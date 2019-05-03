package uk.gov.metoffice.hello.experiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.metoffice.hello.outtray.EnsembleExceedances;
import uk.gov.metoffice.hello.gatekeeper.EnsembleDataReader;
import uk.gov.metoffice.hello.message.Ensemble;
import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;
import uk.gov.metoffice.hello.outtray.ValidBlocksReader;
import uk.gov.metoffice.hello.unit.thresholds.AccumulationThresholdProvider;
import uk.gov.metoffice.hello.outtray.EnsembleThresholder;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class EnsembleThresholderTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

//    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM";
    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_SRENS_ENGWAL_201805270830_SWF_HIM";
    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
    private static final String ENSEMBLE_XML = "grids_ENS00%02d.xml";
    private static final String THRESHOLD_DATA_ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\";


    @Test
    public void makeTestForAllEnsembles() throws IOException {
        List<EnsembleExceedances> ensembleExceedances = IntStream.range(0, 24)
                .mapToObj(i -> String.format(ENSEMBLE_XML, i))
                .map(this::calculateExceededBlocks)
                .collect(Collectors.toList());

        try (PrintWriter printWriter = new PrintWriter("C:\\Workarea\\rjr-ensemble-essay\\src\\test\\resources\\ensembleThresholderTestOutput.json")) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(printWriter, ensembleExceedances);
        }
    }

    private EnsembleExceedances calculateExceededBlocks(String ensembleXmlFileName) {
        // arrange
        System.out.println("*** " + ensembleXmlFileName);
        Ensemble ensemble = EnsembleDataReader.create().readFromXmlFile(ZIP_FILE_NAME, DATA_ROOT, ensembleXmlFileName);
        StormDuration stormDuration = StormDuration.ONE_HOUR;
        int expectedTimestepsInOutput = ensemble.getRunoffFilePerTimestep().size() - stormDuration.getTimeSteps() + 1;
        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
        AccumulationThresholdProvider accumulationThresholdProvider = new AccumulationThresholdProvider(THRESHOLD_DATA_ROOT, validBlocks);
        EnsembleThresholder testObject = new EnsembleThresholder(accumulationThresholdProvider);

        // act
        TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> result = testObject.calculateExceededBlocks(ensemble,
                stormDuration, validBlocks);

        // assert
        assertNotNull(result);

        EnsembleExceedances ensembleExceedances = new EnsembleExceedances(ensembleXmlFileName, result);
        System.out.println(ensembleExceedances);
        validate(ensembleExceedances, expectedTimestepsInOutput);
        return ensembleExceedances;
    }

    private void validate(EnsembleExceedances ensembleExceedances, int expectedTimestepsInOutput) {
        assertEquals(expectedTimestepsInOutput, ensembleExceedances.getThresholdsExceeded().size());
        assertHigherSeveritySubsetOfLower(ensembleExceedances, StormSeverity.ONE_THOUSAND_YEARS, StormSeverity.ONE_HUNDRED_YEARS);
        assertHigherSeveritySubsetOfLower(ensembleExceedances, StormSeverity.ONE_HUNDRED_YEARS, StormSeverity.THIRTY_YEARS);
    }

    private void assertHigherSeveritySubsetOfLower(EnsembleExceedances ensembleExceedances, StormSeverity higher, StormSeverity lower) {
        for (Map.Entry<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> entry : ensembleExceedances.getThresholdsExceeded().entrySet()) {
            EnumMap<StormSeverity, List<Integer>> exceededMap = entry.getValue();
            List<Integer> lowerThresholds = exceededMap.get(lower);
            List<Integer> higherThresholds = exceededMap.get(higher);
            assertFalse(lowerThresholds == null && higherThresholds != null);
            if (lowerThresholds != null && higherThresholds != null) {
                assertTrue(lowerThresholds.containsAll(higherThresholds));
            }
        }
    }

}