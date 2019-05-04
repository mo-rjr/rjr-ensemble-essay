package uk.gov.metoffice.hello.outtray;

import uk.gov.metoffice.hello.domain.*;

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
        StormDuration stormDuration = spec.getStormDuration();
        Ensemble ensemble = spec.getEnsemble();

        // create the accumulated data for this storm length for this area
        Map<ZonedDateTime, Map<Integer, Float>> timestepsToBlockValues = accumulator.accumulateValues(spec);


        Map<ZonedDateTime, Map<Integer, Boolean>> timestepsToBlocksExceedThreshold = thresholder.threshold(spec.getStormDuration(),
                StormSeverity.THIRTY_YEARS,
                spec.getAdminArea().getBlocks(),
                timestepsToBlockValues);



        return Optional.empty();
    }
}
