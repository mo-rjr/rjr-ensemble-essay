package uk.gov.metoffice.hello.gatekeeper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.metoffice.hello.domain.Ensemble;

public class EnsembleDataReaderTest {

    @Test
    public void create() {
        // arrange
        Exception exception = null;

        // act
        try {
            EnsembleDataReader ensembleDataReader = EnsembleDataReader.create();
        } catch (Exception e) {
            exception = e;
        }

        // assert
        Assert.assertNull(exception);
    }

    @Test
    public void readFromXmlFile() throws JsonProcessingException {
        // arrange
        EnsembleDataReader ensembleDataReader = EnsembleDataReader.create();
        String zipFile = "MO_G2G_NWP_NCENS_ENGWAL_201805271830_SWF_HIM_1ENS";
        String DATA_ROOT = "C:\\Workarea\\swf-him-jm\\TestData\\BirminghamFloods20180527\\" + zipFile + "\\";
        String ENSEMBLE_XML = "grids_ENS0000.xml";

        // act
        Ensemble result = ensembleDataReader.readFromXmlFile(zipFile, DATA_ROOT, ENSEMBLE_XML);

        // assert
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        System.out.println(json);
    }
}