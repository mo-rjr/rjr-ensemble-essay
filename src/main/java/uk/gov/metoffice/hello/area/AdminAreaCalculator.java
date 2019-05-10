package uk.gov.metoffice.hello.area;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.metoffice.hello.domain.AdminArea;
import uk.gov.metoffice.hello.domain.ImpactLevel;
import uk.gov.metoffice.hello.domain.ImpactType;
import uk.gov.metoffice.hello.domain.messages.AreaResolverTask;
import uk.gov.metoffice.hello.domain.messages.TimeLocationImpactsPerMember;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AdminAreaCalculator {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final AdminAreaReader ADMIN_AREA_READER = new AdminAreaReader();

    private static final String ADMIN_AREA_FILE = "C:\\Workarea\\rjr-ensemble-essay\\src\\main\\resources\\adminAreas.json";

    private final AreaImpactsSummarizer areaImpactsSummarizer;

    public AdminAreaCalculator(AreaImpactsSummarizer areaImpactsSummarizer) {
        this.areaImpactsSummarizer = areaImpactsSummarizer;
    }

    public TreeMap<Integer, Map<ImpactLevel, Integer>> impactCountsPerArea(AreaResolverTask areaResolverTask) {

        List<AdminArea> adminAreas = ADMIN_AREA_READER.read(ADMIN_AREA_FILE);

        TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> timeLocationImpacts = readImpacts(areaResolverTask.getRefToEnsembleSqKmImpacts());

        TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactLevel, Integer>>> impactsPerArea = areaImpactsSummarizer
                .summarize(adminAreas, timeLocationImpacts);


        return null;

    }

    private TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> readImpacts(String refToEnsembleSqKmImpacts) {

        try (InputStream inputStream = new FileInputStream(refToEnsembleSqKmImpacts)) {
            TimeLocationImpactsPerMember impactsPerMember = OBJECT_MAPPER.readValue(inputStream, TimeLocationImpactsPerMember.class);
            return impactsPerMember.getImpacts();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
            // TODO handle exception properly
        }
    }
}
