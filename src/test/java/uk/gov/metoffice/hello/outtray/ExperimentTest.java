package uk.gov.metoffice.hello.outtray;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.metoffice.hello.gatekeeper.EnsembleDataReader;
import uk.gov.metoffice.hello.domain.Ensemble;
import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormSeverity;
import uk.gov.metoffice.hello.explode.impacts.ImplicationCalculator;
import uk.gov.metoffice.hello.explode.impacts.StormImpactLevelsProvider;
import uk.gov.metoffice.hello.explode.thresholds.AccumulationThresholdProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.NotYetConnectedException;
import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ExperimentTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM";
//    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_SRENS_ENGWAL_201805270830_SWF_HIM";
    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
    private static final String ENSEMBLE_XML = "grids_ENS00%02d.xml";
    private static final String THRESHOLD_DATA_ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\";
    private static final String IMPACT_DATA_ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\ImpactLibrary\\NineScenario_v01\\";


    @Test
    public void makeTestForAllEnsembles() throws IOException {
        List<NotableImpacts> notableImpactsList = IntStream.range(0, 24)
                .mapToObj(i -> String.format(ENSEMBLE_XML, i))
                .map(this::calculateExceededBlocks)
                .collect(Collectors.toList());

        try (PrintWriter printWriter = new PrintWriter("C:\\Workarea\\rjr-ensemble-essay\\ensembleExperimentTestOutput.json")) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(printWriter, notableImpactsList);
        }

        ExperimentTestImpactsChecker impactsChecker = new ExperimentTestImpactsChecker(IMPACT_DATA_ROOT);
        impactsChecker.checkEmptyMapsMeanUnimportantPlaces(notableImpactsList);
    }


    @Test
    public void makeTestForOneEnsemble() throws IOException {

        NotableImpacts notableImpacts = calculateExceededBlocks(String.format(ENSEMBLE_XML, 5));
        //
        assertNotNull(notableImpacts);

        // TODO here you want impact
        try (PrintWriter printWriter = new PrintWriter("C:\\Workarea\\rjr-ensemble-essay\\notableImpacts.json")) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(printWriter, notableImpacts);
        }
    }

    private NotableImpacts calculateExceededBlocks(String ensembleXmlFileName) {
        // arrange
        System.out.println("*** " + ensembleXmlFileName);
        Ensemble ensemble = EnsembleDataReader.create().readFromXmlFile(ZIP_FILE_NAME, DATA_ROOT, ensembleXmlFileName);
        StormDuration stormDuration = StormDuration.ONE_HOUR;
        int expectedTimestepsInOutput = ensemble.getRunoffFilePerTimestep().size() - stormDuration.getTimeSteps() + 1;
        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
        AccumulationThresholdProvider accumulationThresholdProvider = new AccumulationThresholdProvider(THRESHOLD_DATA_ROOT, validBlocks);
        EnsembleThresholder ensembleThresholder = new EnsembleThresholder(accumulationThresholdProvider);

        StormImpactLevelsProvider stormImpactLevelsProvider = new StormImpactLevelsProvider(IMPACT_DATA_ROOT, validBlocks);
        ImplicationCalculator implicationCalculator = new ImplicationCalculator(stormImpactLevelsProvider);

        // act
        TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> result = ensembleThresholder.calculateExceededBlocks(ensemble,
                stormDuration, validBlocks);

        // assert
        assertNotNull(result);

        EnsembleExceedances ensembleExceedances = new EnsembleExceedances(ensembleXmlFileName, result);

//        TreeMap<ZonedDateTime, EnumMap<StormSeverity, Map<Integer, EnumMap<ImpactType, Short>>>> output = implicationCalculator.calculateImpacts(ensembleExceedances, stormDuration);

//        return new NotableImpacts(ensembleXmlFileName, output);
        throw new NotYetConnectedException();
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
