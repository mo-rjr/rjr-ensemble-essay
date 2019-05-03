package uk.gov.metoffice.hello.experiment;

import uk.gov.metoffice.hello.outtray.NotableImpacts;
import uk.gov.metoffice.hello.unit.AdvancingBilFileReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class ExperimentTestImpactsChecker {

    private static final List<String> FILES_TO_CHECK = Arrays.asList(
            "ksinfra_1hr_30_1", "ksinfra_1hr_100_1", "ksinfra_1hr_1000_1",
            "max_1hr_30_1", "max_1hr_100_1", "max_1hr_1000_1",
            "trans_1hr_30_1", "trans_1hr_100_1", "trans_1hr_1000_1",
            "pop_1hr_30_1", "pop_1hr_100_1", "pop_1hr_1000_1",
            "prop_1hr_30_1", "prop_1hr_100_1", "prop_1hr_1000_1");

    private final String root;

    public ExperimentTestImpactsChecker(String root) {
        this.root = root;
    }

    public void checkEmptyMapsMeanUnimportantPlaces(List<NotableImpacts> notableImpactList) throws IOException {

        TreeSet<Integer> unimportantPlaces = notableImpactList.stream()
                .flatMap(ni -> ni.getImpacts().values().stream())
                .flatMap(stormSeverityMapEnumMap -> stormSeverityMapEnumMap.values().stream())
                .flatMap(integerEnumMapMap -> integerEnumMapMap.entrySet().stream())
                .filter(entry -> entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(TreeSet::new));


        for (String file : FILES_TO_CHECK) {
            try (AdvancingBilFileReader<Short> reader = AdvancingBilFileReader.forShorts(root + file +".bil", 540)) {
                for (Integer place : unimportantPlaces) {
                    float value = reader.readNext(place);
                    if (value > 0.0f) {
                        System.out.println("In file " + file + " place " + place + " has value " + value);
                    }
                }
            }
        }

    }

}
