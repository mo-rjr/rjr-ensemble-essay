package uk.gov.metoffice.hello.area;

import uk.gov.metoffice.hello.domain.AdminArea;
import uk.gov.metoffice.hello.domain.ImpactLevel;
import uk.gov.metoffice.hello.domain.ImpactType;

import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.TreeMap;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class AreaImpactsSummarizer {
    public TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactLevel, Integer>>> summarize(List<AdminArea> adminAreas,
                                                                                             TreeMap<ZonedDateTime, TreeMap<Integer, EnumMap<ImpactType, Short>>> timeLocationImpacts) {
//
//        timeLocationImpacts.entrySet().stream()
//                .collect(Collectors.toMap(Map.Entry::getKey,
//                        ))

        return null;
    }

//    private TreeMap<Integer, EnumMap<ImpactLevel, Integer>> perCounty() {
//
//    }
}
