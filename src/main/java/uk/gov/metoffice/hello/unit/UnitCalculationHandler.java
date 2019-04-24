package uk.gov.metoffice.hello.unit;

import uk.gov.metoffice.hello.message.*;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class UnitCalculationHandler {

    private final Accumulator accumulator;
    private final Thresholder thresholder;

    public UnitCalculationHandler(Accumulator accumulator, Thresholder thresholder) {
        this.accumulator = accumulator;
        this.thresholder = thresholder;
    }

    public Optional<UnitOutput> handleCalculation(OneDurationOneEnsembleOneArea spec) {
        AdminArea adminArea = spec.getAdminArea();
        StormDuration stormDuration = spec.getDuration();
        Ensemble ensemble = spec.getEnsemble();

        // create the accumulated data for this storm length for this area
        Map<ZonedDateTime, Map<Integer, Float>> timestepsToBlockValues = accumulator.accumulateValues(spec);


        Map<ZonedDateTime, Map<Integer, Boolean>> timestepsToBlocksExceedThreshold = thresholder.threshold(timestepsToBlockValues);


        return Optional.empty();
    }
}
