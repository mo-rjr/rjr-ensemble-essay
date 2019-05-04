package uk.gov.metoffice.hello.domain;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public enum StormDuration {

    ONE_HOUR(4),
    THREE_HOURS(12),
    SIX_HOURS(24);

    private static final Map<Integer, StormDuration> TIME_STEPS_TO_STORM_DURATION =
            Stream.of(values()).collect(toMap(StormDuration::getTimeSteps, e -> e));

    int timeSteps;

    StormDuration(int timeSteps) {
        this.timeSteps = timeSteps;
    }

    public int getTimeSteps() {
        return timeSteps;
    }

    public int getHours() {
        return timeSteps / 4;
    }

    public static Optional<StormDuration> getStormDuration(int timeSteps) {
        return Optional.ofNullable(TIME_STEPS_TO_STORM_DURATION.get(timeSteps));
    }

}


