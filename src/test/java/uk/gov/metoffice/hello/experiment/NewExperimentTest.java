package uk.gov.metoffice.hello.experiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.metoffice.hello.gatekeeper.EnsembleDataReader;
import uk.gov.metoffice.hello.domain.Ensemble;
import uk.gov.metoffice.hello.domain.ImpactType;
import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormReturnPeriod;
import uk.gov.metoffice.hello.explode.ValidBlocksReader;
import uk.gov.metoffice.hello.domain.messages.TimeLocationImpactsPerMember;
import uk.gov.metoffice.hello.domain.TimeLocationStorms;
import uk.gov.metoffice.hello.explode.impacts.ImpactCalculator;
import uk.gov.metoffice.hello.explode.impacts.StormImpactLevelsProvider;
import uk.gov.metoffice.hello.explode.thresholds.AccumulationThresholdProvider;
import uk.gov.metoffice.hello.explode.thresholds.MaxIntensityEnsembleThresholder;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertNotNull;

public class NewExperimentTest {
    // TODO this one is real, get rid of the others

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271630_SWF_HIM";
//    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM";
//    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_SRENS_ENGWAL_201805270830_SWF_HIM";
    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
    private static final String ENSEMBLE_XML = "grids_ENS00%02d.xml";
    private static final String THRESHOLD_DATA_ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\";
    private static final String IMPACT_DATA_ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\ImpactLibrary\\NineScenario_v01\\";


    @Test
    public void makeTestForAllEnsembles() throws IOException {
        List<TimeLocationImpactsPerMember> timeLocationImpactsPerMemberList = IntStream.range(0, 24)
                .mapToObj(i -> String.format(ENSEMBLE_XML, i))
                .map(this::calculateExceededBlocks)
                .collect(Collectors.toList());

        try (PrintWriter printWriter = new PrintWriter("C:\\Workarea\\rjr-ensemble-essay\\src\\test\\resources\\timeLocationImpacts.json")) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(printWriter, timeLocationImpactsPerMemberList);
        }

//        ExperimentTestImpactsChecker impactsChecker = new ExperimentTestImpactsChecker(IMPACT_DATA_ROOT);
//        impactsChecker.checkEmptyMapsMeanUnimportantPlaces(notableImpactsList);
    }


    @Test
    public void makeTestForOneEnsemble() throws IOException {

        TimeLocationImpactsPerMember timeLocationImpactsPerMember = calculateExceededBlocks(String.format(ENSEMBLE_XML, 5));
        //
        assertNotNull(timeLocationImpactsPerMember);

        try (PrintWriter printWriter = new PrintWriter("C:\\Workarea\\rjr-ensemble-essay\\src\\test\\resources\\timeLocationImpacts5.json")) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(printWriter, timeLocationImpactsPerMember);
        }
    }

    private TimeLocationImpactsPerMember calculateExceededBlocks(String ensembleXmlFileName) {
        // arrange
        System.out.println("*** " + ensembleXmlFileName);
        Ensemble ensemble = EnsembleDataReader.create().readFromXmlFile(ZIP_FILE_NAME, DATA_ROOT, ensembleXmlFileName);
        StormDuration stormDuration = StormDuration.ONE_HOUR;
        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
        AccumulationThresholdProvider accumulationThresholdProvider = new AccumulationThresholdProvider(THRESHOLD_DATA_ROOT, validBlocks);
        MaxIntensityEnsembleThresholder maxIntensityEnsembleThresholder = new MaxIntensityEnsembleThresholder(accumulationThresholdProvider);
        StormImpactLevelsProvider stormImpactLevelsProvider = new StormImpactLevelsProvider(IMPACT_DATA_ROOT, validBlocks);
        ImpactCalculator impactCalculator = new ImpactCalculator(stormImpactLevelsProvider);

        // act
        TreeMap<ZonedDateTime, TreeMap<Integer, StormReturnPeriod>> intermediaryResult = maxIntensityEnsembleThresholder.calculateExceededBlocks(ensemble,
                stormDuration, validBlocks);
        TimeLocationStorms timeLocationStorms = new TimeLocationStorms(ensembleXmlFileName, intermediaryResult);
        TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> result = impactCalculator.calculateImpacts(timeLocationStorms, stormDuration);

        // assert
        assertNotNull(intermediaryResult);
        assertNotNull(result);

        return new TimeLocationImpactsPerMember(ensembleXmlFileName, result);
    }

//    private void validate(EnsembleExceedances ensembleExceedances, int expectedTimestepsInOutput) {
//        assertEquals(expectedTimestepsInOutput, ensembleExceedances.getThresholdsExceeded().size());
//        assertHigherSeveritySubsetOfLower(ensembleExceedances, StormReturnPeriod.ONE_THOUSAND_YEARS, StormReturnPeriod.ONE_HUNDRED_YEARS);
//        assertHigherSeveritySubsetOfLower(ensembleExceedances, StormReturnPeriod.ONE_HUNDRED_YEARS, StormReturnPeriod.THIRTY_YEARS);
//    }
//
//    private void assertHigherSeveritySubsetOfLower(EnsembleExceedances ensembleExceedances, StormReturnPeriod higher, StormReturnPeriod lower) {
//        for (Map.Entry<ZonedDateTime, EnumMap<StormReturnPeriod, List<Integer>>> entry : ensembleExceedances.getThresholdsExceeded().entrySet()) {
//            EnumMap<StormReturnPeriod, List<Integer>> exceededMap = entry.getShortValue();
//            List<Integer> lowerThresholds = exceededMap.get(lower);
//            List<Integer> higherThresholds = exceededMap.get(higher);
//            assertFalse(lowerThresholds == null && higherThresholds != null);
//            if (lowerThresholds != null && higherThresholds != null) {
//                assertTrue(lowerThresholds.containsAll(higherThresholds));
//            }
//        }
//    }

}
