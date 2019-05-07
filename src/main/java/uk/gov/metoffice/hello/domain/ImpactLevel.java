package uk.gov.metoffice.hello.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public enum ImpactLevel {

    NONE(0),
    MINIMAL(1),
    MINOR(2),
    MAJOR(3),
    SEVERE(4);

    public static Map<Short, ImpactLevel> SHORT_IMPACT_LEVEL_MAP = new HashMap<>();

    short shortValue;

    ImpactLevel(Integer value) {
        this.shortValue = value.shortValue();
    }

    public static ImpactLevel forShort(short number) {
        if (SHORT_IMPACT_LEVEL_MAP.isEmpty()) {
            SHORT_IMPACT_LEVEL_MAP.putAll(Arrays.stream(values())
                    .collect(Collectors.toMap(ImpactLevel::getShortValue, i -> i)));
        }
        return SHORT_IMPACT_LEVEL_MAP.get(number);
    }

    public short getShortValue() {
        return shortValue;
    }

}
