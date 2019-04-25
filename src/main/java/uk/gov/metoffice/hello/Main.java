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
    private static final String ZIP_FILE_NAME = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM_1ENS";
    private static final String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + ZIP_FILE_NAME + "\\";
    private static final String ENSEMBLE_XML = "grids_ENS0000.xml";

    private static final int S_GLOUCS = 66;
    private static final int W_MIDLANDS = 4;
    private static final int GTR_LONDON = 6;

    public static void main(String[] args) {

        OneDurationOneEnsembleOneArea spec = setUpSpecification();

        Accumulator accumulator = new Accumulator(new ReadValuesForBlocks<>(ByteBuffer::getFloat,
                Float.BYTES, 540));
        Thresholder thresholder = new Thresholder(new ReadValuesForBlocks<>(ByteBuffer::getFloat,
                Float.BYTES, 540));
        UnitCalculationHandler unitCalculationHandler = new UnitCalculationHandler(accumulator, thresholder);

        Map<ZonedDateTime, Map<Integer, Float>> accumulated = accumulator.accumulateValues(spec);
        Map<ZonedDateTime, Map<Integer, Boolean>> output = thresholder.threshold(spec.getStormDuration(),
                StormSeverity.THIRTY_YEARS,
                spec.getAdminArea().getBlocks(),
                accumulated);
        System.out.println(output.size());

        int countAll = 0;
        int countTrue = 0;

        for (Map<Integer, Boolean> crossedByBlock : output.values()) {
            for (Boolean crossed : crossedByBlock.values()) {
                countAll++;
                if (crossed) {
                    countTrue++;
                }
            }

        }
        System.out.println(countTrue + " thresholds crossed out of " + countAll);

    }

    private static OneDurationOneEnsembleOneArea setUpSpecification() {
        String zipFile = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM_1ENS";

        // set up duration
//        StormDuration stormDuration = StormDuration.ONE_HOUR;
        StormDuration stormDuration = StormDuration.SIX_HOURS;

        // set up ensemble
        EnsembleDataReader ensembleDataReader = EnsembleDataReader.create();
        Ensemble ensemble = ensembleDataReader.readFromXmlFile(zipFile, DATA_ROOT, ENSEMBLE_XML);

        // set up area
        AdminAreaReader adminAreaReader = new AdminAreaReader();
        List<AdminArea> adminAreaList = adminAreaReader.read(ADMIN_AREA_FILE);
        AdminArea adminArea = adminAreaList.stream()
                .filter(area -> area.getId() == GTR_LONDON)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        return new OneDurationOneEnsembleOneArea(zipFile,
                stormDuration, ensemble, adminArea);
    }
}
