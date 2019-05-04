package uk.gov.metoffice.hello.explode.thresholds;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AccumulationThresholdProviderTest {

//    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM";
//    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
//    private static final String ENSEMBLE_XML = "grids_ENS0013.xml";
//    private static final String THRESHOLD_DATA_ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\";
//
//
//    @Test
//    public void getFor() {
//        // arrange
////        Ensemble ensemble = EnsembleDataReader.create().readFromXmlFile(ZIP_FILE_NAME, DATA_ROOT, ENSEMBLE_XML);
//        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
//        AccumulationThresholdProvider testObject = new AccumulationThresholdProvider(THRESHOLD_DATA_ROOT, validBlocks);
//
//        // act
//        AccumulationThresholder result = testObject.getFor(StormDuration.ONE_HOUR, StormSeverity.THIRTY_YEARS);
//
//        // assert
//        assertNotNull(result);
//        System.out.println(result.getThresholdsPerBlock().size());
//        List<Integer> noDataPositions = result.getThresholdsPerBlock().entrySet().stream().filter(entry -> entry.getValue() < 0).map(Map.Entry::getKey).collect(Collectors.toList());
//        System.out.println("No data values at " + noDataPositions.size());
//        List<Integer> zeroPositions = result.getThresholdsPerBlock().entrySet().stream().filter(entry -> entry.getValue() == 0).map(Map.Entry::getKey).collect(Collectors.toList());
//        System.out.println("Zero data values at " + zeroPositions.size());
//        System.out.println(result.getFileName());
//    }
}