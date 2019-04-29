package uk.gov.metoffice.hello;

import uk.gov.metoffice.hello.gatekeeper.AdminAreaReader;
import uk.gov.metoffice.hello.gatekeeper.EnsembleDataReader;
import uk.gov.metoffice.hello.message.*;
import uk.gov.metoffice.hello.unit.Accumulator;
import uk.gov.metoffice.hello.unit.ReadValuesForBlocks;
import uk.gov.metoffice.hello.unit.Thresholder;
import uk.gov.metoffice.hello.unit.UnitCalculationHandler;

import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class Main {

    private static final String ADMIN_AREA_FILE = "C:\\Workarea\\rjr-ensemble-essay\\src\\main\\resources\\adminAreas.json";
    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM";
    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
    private static final String ENSEMBLE_XML = "grids_ENS0013.xml";


    private static final int W_MIDLANDS = 4;
    private static final int GTR_LONDON = 6;
    private static final int STOKE = 62;
    private static final int S_GLOUCS = 66;

    public static void main(String[] args) {

        Accumulator accumulator = new Accumulator(new ReadValuesForBlocks<>(ByteBuffer::getFloat,
                Float.BYTES, 540));
        Thresholder thresholder = new Thresholder(new ReadValuesForBlocks<>(ByteBuffer::getFloat,
                Float.BYTES, 540));
        UnitCalculationHandler unitCalculationHandler = new UnitCalculationHandler(accumulator, thresholder);

        StormDuration stormDuration = StormDuration.ONE_HOUR;

        OneDurationOneEnsembleOneArea spec = setUpSpecification(stormDuration);


        Map<ZonedDateTime, Map<Integer, Float>> accumulated = accumulator.accumulateValues(spec);
        Map<ZonedDateTime, Map<Integer, Boolean>> output = thresholder.threshold(spec.getStormDuration(),
                StormSeverity.THIRTY_YEARS,
                spec.getAdminArea().getBlocks(),
                accumulated);
        System.out.println(output.size());

        int countAll = 0;
        int countTrue = 0;

        for (Map.Entry<ZonedDateTime, Map<Integer, Boolean>> entry : output.entrySet()) {
            ZonedDateTime zonedDateTime = entry.getKey();
            for (Map.Entry<Integer, Boolean> perTime : entry.getValue().entrySet()) {
                countAll++;
                if (perTime.getValue()) {
                    countTrue++;
                    System.out.println(zonedDateTime + ", block " + perTime.getKey());
                }
            }

        }
        System.out.println(countTrue + " thresholds crossed out of " + countAll);

    }

    private static OneDurationOneEnsembleOneArea setUpSpecification(StormDuration stormDuration) {
        String zipFile = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM_1ENS";

        // set up duration
//        StormDuration stormDuration = StormDuration.ONE_HOUR;
//        StormDuration stormDuration = StormDuration.SIX_HOURS;

        // set up ensemble
        EnsembleDataReader ensembleDataReader = EnsembleDataReader.create();
        Ensemble ensemble = ensembleDataReader.readFromXmlFile(zipFile, DATA_ROOT, ENSEMBLE_XML);

        // set up area
        AdminAreaReader adminAreaReader = new AdminAreaReader();
        List<AdminArea> adminAreaList = adminAreaReader.read(ADMIN_AREA_FILE);
        AdminArea adminArea = adminAreaList.stream()
                .filter(area -> area.getId() == 13)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        System.out.println("Admin area " + adminArea.getId() + " is " + adminArea.getName());

        return new OneDurationOneEnsembleOneArea(zipFile,
                stormDuration, ensemble, adminArea);
    }
}
