package uk.gov.metoffice.hello.explode.impacts;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ImplicationCalculatorTest {

//    private static final String ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\ImpactLibrary\\NineScenario_v01\\";
//
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
//
//    @Test
//    public void calculateImpacts() throws IOException {
//        // arrange
//        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
//        StormImpactLevelsProvider stormImpactLevelsProvider = new StormImpactLevelsProvider(ROOT, validBlocks);
//        ImpactCalculator testObject = new ImpactCalculator(stormImpactLevelsProvider);
//        TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> thresholdsExceeded = makeTheMap();
////        EnsembleExceedances ensembleExceedances = new EnsembleExceedances("testEnsemble",
////                thresholdsExceeded);
//        EnsembleExceedances ensembleExceedances = readFrom("ensembleThresholderTestOutput.json");
//
//        // act
//
//        TreeMap<ZonedDateTime, EnumMap<StormSeverity, Map<Integer, EnumMap<ImpactType, Short>>>> result = testObject.calculateImpacts(ensembleExceedances, StormDuration.ONE_HOUR);
//
//        // assert
//        assertNotNull(result);
//    }
//
//    private TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> makeTheMap() {
//        List<Integer> someIntegers = Arrays.asList(160640,160644,161184,161185);
//        EnumMap<StormSeverity, List<Integer>> severities = new EnumMap<>(StormSeverity.class);
//        severities.put(StormSeverity.THIRTY_YEARS, someIntegers);
//        severities.put(StormSeverity.ONE_HUNDRED_YEARS, someIntegers);
//        severities.put(StormSeverity.ONE_THOUSAND_YEARS, someIntegers);
//        TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> output = new TreeMap<>();
//        output.put(ZonedDateTime.of(LocalDateTime.of(2019, Month.MAY, 1, 12, 0), ZoneId.of("UTC")), severities);
//        return output;
//    }
//
//    private EnsembleExceedances readFrom(String fileName) throws IOException {
//        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
//            return OBJECT_MAPPER.readValue(inputStream, EnsembleExceedances.class);
//        }
//    }
}