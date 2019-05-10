package uk.gov.metoffice.hello.explode.impacts;

import uk.gov.metoffice.hello.domain.ImpactType;
import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormReturnPeriod;
import uk.gov.metoffice.hello.explode.AdvancingBilFileReader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.metoffice.hello.domain.BilConstants.ROW_LENGTH;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class StormImpactLevelsProvider {

    // C:\Useful\SWF_HIM\SWF-Mashup\SWFHIM\NCENS_Live\processing\SupplementaryData\ImpactLibrary\NineScenario_v01\

    private static final String IMPACT_FILE_KEY = "${IMPACT_FILE_PART}";
    private static final String DURATION_HOURS_KEY = "${DURATION_HOURS}";
    private static final String SEVERITY_YEARS_KEY = "${SEVERITY_YEARS}";
    private static final String FILE_TEMPLATE = IMPACT_FILE_KEY + "_" + DURATION_HOURS_KEY + "hr_" + SEVERITY_YEARS_KEY + "_1.bil";

    //    private final Map<StormDefinition, StormImpactLevels> impactLevels = new HashMap<>();
    private final Map<StormDuration, StormImpactLevels> impactLevelsByDuration = new EnumMap<>(StormDuration.class);

    private final String thresholdFolderRoot;

    private final List<Integer> validBlocks;

    public StormImpactLevelsProvider(String thresholdFolderRoot, List<Integer> validBlocks) {
        this.thresholdFolderRoot = thresholdFolderRoot;
        this.validBlocks = validBlocks;
    }

    // lazily populates map
    public StormImpactLevels getFor(StormDuration stormDuration) {
        return impactLevelsByDuration.computeIfAbsent(stormDuration,
                this::createImpactLevelsFor);
    }

    private StormImpactLevels createImpactLevelsFor(StormDuration stormDuration) {
        StormImpactLevels stormImpactLevels = new StormImpactLevels(stormDuration);
        for (StormReturnPeriod stormReturnPeriod : StormReturnPeriod.values()) {
            for (ImpactType impact : ImpactType.values()) {
                String fileName = thresholdFolderRoot + fileNameFor(stormDuration, stormReturnPeriod, impact);
                try (AdvancingBilFileReader<Short> bilFileReader = AdvancingBilFileReader.forShorts(fileName, ROW_LENGTH)) {
                    Map<Integer, Short> thresholdValues = new HashMap<>();
                    for (Integer block : validBlocks) {
                        thresholdValues.put(block, bilFileReader.readNext(block));
                    }
                    stormImpactLevels.put(stormReturnPeriod, impact, thresholdValues);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                    // todo think about this pretty hard
                }
            }
        }
        return stormImpactLevels;
    }

    private String fileNameFor(StormDuration stormDuration, StormReturnPeriod stormReturnPeriod, ImpactType impact) {
        String impactFilePart = impact.getFileNamePart();
        String durationHours = Integer.toString(stormDuration.getHours());
        String stormYears = Integer.toString(stormReturnPeriod.getYears());
        return FILE_TEMPLATE
                .replace(IMPACT_FILE_KEY, impactFilePart)
                .replace(DURATION_HOURS_KEY, durationHours)
                .replace(SEVERITY_YEARS_KEY, stormYears);
    }


}
