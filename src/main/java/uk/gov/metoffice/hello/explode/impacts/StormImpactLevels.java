package uk.gov.metoffice.hello.explode.impacts;

import uk.gov.metoffice.hello.domain.ImpactType;
import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormReturnPeriod;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * This class knows about the impacts for a particular storm duration
 * When told a Storm Severity, it can get all the impacts for a location for that Storm Duration
 */
public class StormImpactLevels {

    private final StormDuration stormDuration;

    private final short ZERO = Short.parseShort("0");

    private final EnumMap<StormReturnPeriod, EnumMap<ImpactType, Map<Integer, Short>>> impactLevels = new EnumMap<>(StormReturnPeriod.class);

    public StormImpactLevels(StormDuration stormDuration) {
        this.stormDuration = stormDuration;
    }

    /**
     * Returns the impacts for this affected area at this storm severity (which is the calculated max)
     * @param affectedBlock sq km affected
     * @param stormReturnPeriod max value for the intensity of the stor
     * @return a map of impact type to value, empty if none
     */
    public EnumMap<ImpactType, Short> getImpacts(Integer affectedBlock, StormReturnPeriod stormReturnPeriod) {
        EnumMap<ImpactType, Short> consequencesForThisBlock = new EnumMap<>(ImpactType.class);
        EnumMap<ImpactType, Map<Integer, Short>> levelsPerSeverity = impactLevels.getOrDefault(stormReturnPeriod,
                new EnumMap<>(ImpactType.class));
        for (ImpactType impactType : ImpactType.values()) {
            short impactForThisBlock = levelsPerSeverity.getOrDefault(impactType, new HashMap<>())
                    .getOrDefault(affectedBlock, ZERO);
            if (impactForThisBlock > ZERO) {
                consequencesForThisBlock.put(impactType, impactForThisBlock);
            }
        }
        return consequencesForThisBlock;
    }


    public void put(StormReturnPeriod stormReturnPeriod, ImpactType impact, Map<Integer, Short> thresholdValues) {
        impactLevels.computeIfAbsent(stormReturnPeriod, s -> new EnumMap<>(ImpactType.class))
                .put(impact, thresholdValues);
    }

    public EnumMap<StormReturnPeriod, EnumMap<ImpactType, Map<Integer, Short>>> getImpactLevels() {
        return impactLevels;
    }

    public StormDuration getStormDuration() {
        return stormDuration;
    }

}
