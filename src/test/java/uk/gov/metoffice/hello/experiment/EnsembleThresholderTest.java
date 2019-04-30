package uk.gov.metoffice.hello.experiment;

import org.junit.Test;
import uk.gov.metoffice.hello.gatekeeper.EnsembleDataReader;
import uk.gov.metoffice.hello.message.Ensemble;
import uk.gov.metoffice.hello.message.StormDuration;
import uk.gov.metoffice.hello.message.StormSeverity;

import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class EnsembleThresholderTest {

    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM";
    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
    private static final String ENSEMBLE_XML = "grids_ENS0013.xml";
    private static final String THRESHOLD_DATA_ROOT = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\";

    @Test
    public void calculateExceededBlocks() {
        // arrange
        Ensemble ensemble = EnsembleDataReader.create().readFromXmlFile(ZIP_FILE_NAME, DATA_ROOT, ENSEMBLE_XML);
        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
        AccumulationThresholdProvider accumulationThresholdProvider = new AccumulationThresholdProvider(THRESHOLD_DATA_ROOT, validBlocks);
        EnsembleThresholder testObject = new EnsembleThresholder(accumulationThresholdProvider);

        // act
        Map<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> result = testObject.calculateExceededBlocks(ensemble, StormDuration.ONE_HOUR, validBlocks);

        // assert
        assertNotNull(result);
        System.out.println(printable(result));


    }

    private String printable(Map<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> result) {
        TreeMap<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> resultOrdered = new TreeMap<>(result);
        return resultOrdered.entrySet()
                .stream()
                .map(this::oneEntryToString)
                .collect(Collectors.joining("\n"));
    }

    private String oneEntryToString(Map.Entry<ZonedDateTime, EnumMap<StormSeverity, List<Integer>>> zonedDateTimeEnumMapEntry) {
        String enumMapString = zonedDateTimeEnumMapEntry.getValue().entrySet().stream()
                .map(this::severityMapToString)
                .collect(Collectors.joining("\n"));
        return zonedDateTimeEnumMapEntry.getKey() + "\n" + enumMapString;
    }

    private String severityMapToString(Map.Entry<StormSeverity, List<Integer>> stormSeverityListEntry) {
        int entryCount = stormSeverityListEntry.getValue().size();
        String message = "    " + stormSeverityListEntry.getKey() + " has " + (entryCount == 1 ? "1 entry" : entryCount + " entries");
        if (stormSeverityListEntry.getValue().size() < 10) {
            message += "\n      ";
            message += stormSeverityListEntry.getValue()
                    .stream()
                    .map(i -> Integer.toString(i))
                    .collect(Collectors.joining(","));
        }

        return message;
    }
}