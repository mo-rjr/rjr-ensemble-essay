package uk.gov.metoffice.hello.experiment;

import org.junit.Test;
import uk.gov.metoffice.hello.gatekeeper.AdminAreaReader;
import uk.gov.metoffice.hello.message.AdminArea;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AdvancingBilFileReaderTest {
    private static final String ADMIN_AREA_FILE = "C:\\Workarea\\rjr-ensemble-essay\\src\\main\\resources\\adminAreas.json";

    @Test
    public void readNextValidBlocks() throws IOException {

        // arrange
        String fileName = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\p30_1hr_a.bil";
        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
        int noDataCount = 0;

        try (AdvancingBilFileReader<Float> testObject = new AdvancingBilFileReader<>(ByteBuffer::getFloat, fileName, 540, Float.BYTES)) {

            for (int step = 0; step < validBlocks.size(); step++) {
                // act
                Float result = testObject.readNext(validBlocks.get(step));


                if (result <= 0.0) {
                    System.out.println("Position " + validBlocks.get(step) + " is " + result);
                    noDataCount++;
                }
            }

        }

        System.out.println("There are " + noDataCount + " no-data values out of " + validBlocks.size());

    }

    @Test
    public void readNextAllBlocks() throws IOException {

        // arrange
        String root = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\";
        List<String> fileNames = Arrays.asList("p30_1hr_a.bil", "p30_3hr_a.bil", "p30_6hr_a.bil",
                "p100_1hr_a.bil", "p100_3hr_a.bil", "p100_6hr_a.bil",
                "p1000_1hr_a.bil", "p1000_3hr_a.bil", "p1000_6hr_a.bil");
        int sizeOfData = 540 * 700;

        for (String fileName : fileNames) {

            try (AdvancingBilFileReader<Float> testObject = new AdvancingBilFileReader<>(ByteBuffer::getFloat, root + fileName, 540, Float.BYTES)) {

                List<Integer> haveValues = new ArrayList<>();
                for (int step = 0; step < sizeOfData; step++) {
                    Float result = testObject.readNext(step);
                    if (result >= 0.0) {
                        haveValues.add(step);
                    }
                }

                System.out.println(fileName + " has " + haveValues.size() + " values with data out of " + sizeOfData);

            }
        }
    }



    @Test
    public void whatBlocksAreMissingPerCounty() throws IOException {
        // arrange
        String fileName = "C:\\Useful\\SWF_HIM\\SWF-Mashup\\SWFHIM\\NCENS_Live\\processing\\SupplementaryData\\FlowThresholds\\p30_1hr_a.bil";
        List<Integer> validBlocks = new ValidBlocksReader().readValidBlocks();
        AdminAreaReader adminAreaReader = new AdminAreaReader();
        List<AdminArea> adminAreaList = adminAreaReader.read(ADMIN_AREA_FILE);

        List<Integer> noValues = new ArrayList<>();
        try (AdvancingBilFileReader<Float> testObject = new AdvancingBilFileReader<>(ByteBuffer::getFloat, fileName, 540, Float.BYTES)) {
            for (int step = 0; step < validBlocks.size(); step++) {
                // act
                Float result = testObject.readNext(validBlocks.get(step));
                if (result <= 0.0) {
                    noValues.add(validBlocks.get(step));
                }
            }
        }

        for (AdminArea adminArea : adminAreaList) {
            TreeSet<Integer> missingSteps = new TreeSet<>(adminArea.getBlocks());
            missingSteps.retainAll(noValues);


            System.out.println(adminArea.getName() + " has " + missingSteps.size() + " missing values");

        }



    }
}