package uk.gov.metoffice.hello.domain.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.metoffice.hello.domain.ImpactLevel;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertFalse;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class EnsembleAreaImpactsTallyTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void tryOutPrinting() throws IOException {
        // arrange
        String contextId = "MO_G2G_NWP_NCENS_ENGWAL_201805271630_SWF_HIM";
        String ensembleId = "ENS0005.xml";
        Map<Integer, Map<ImpactLevel, Integer>> areaImpactTallies = new HashMap<>();
        TreeMap<ImpactLevel, Integer> impactsForAreaOne = new TreeMap<>();
        impactsForAreaOne.put(ImpactLevel.MINIMAL, 13);
        impactsForAreaOne.put(ImpactLevel.MINOR, 8);
        impactsForAreaOne.put(ImpactLevel.SIGNIFICANT, 5);
        impactsForAreaOne.put(ImpactLevel.SEVERE, 4);
        areaImpactTallies.put(25, impactsForAreaOne);
        EnsembleAreaImpactsTally ensembleAreaImpactsTally = new EnsembleAreaImpactsTally(contextId, ensembleId, areaImpactTallies);

        // act
        StringWriter stringWriter = new StringWriter();
        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(stringWriter, ensembleAreaImpactsTally);
        String output = stringWriter.toString();

        // assert
        System.out.println(output);
        assertFalse(output.isEmpty());
    }
}