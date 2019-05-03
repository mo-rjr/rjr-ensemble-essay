package uk.gov.metoffice.hello.message;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class StormDefinition {
    private StormDuration stormDuration;

    private StormSeverity stormSeverity;

    public StormDefinition(StormDuration stormDuration, StormSeverity stormSeverity) {
        this.stormDuration = stormDuration;
        this.stormSeverity = stormSeverity;
    }

    public static List<StormDefinition> allPossible() {
        return Arrays.stream(StormDuration.values())
                .flatMap(duration -> Arrays.stream(StormSeverity.values())
                        .map(severity -> new StormDefinition(duration, severity)))
                .collect(Collectors.toList());
    }

    public StormDuration getStormDuration() {
        return stormDuration;
    }

    public StormSeverity getStormSeverity() {
        return stormSeverity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StormDefinition that = (StormDefinition) o;
        return stormDuration == that.stormDuration &&
                stormSeverity == that.stormSeverity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stormDuration, stormSeverity);
    }
}
