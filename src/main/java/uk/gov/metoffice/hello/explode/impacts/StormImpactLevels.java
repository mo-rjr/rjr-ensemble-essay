package uk.gov.metoffice.hello.explode.impacts;

import uk.gov.metoffice.hello.domain.ImpactType;
import uk.gov.metoffice.hello.domain.StormDuration;
import uk.gov.metoffice.hello.domain.StormSeverity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * {A thing} to {do something} for {another thing}
 * -- for example, {this}
 * -- and also {this}
 */
// TODO fill in Javadoc
public class StormImpactLevels {

    private final StormDuration stormDuration;

    private final short ZERO = Short.parseShort("0");

    //    private final EnumMap<ImpactType, Map<Integer, Short>> impactLevels = new EnumMap<>(ImpactType.class);
    private final EnumMap<StormSeverity, EnumMap<ImpactType, Map<Integer, Short>>> impactLevels = new EnumMap<>(StormSeverity.class);

    public StormImpactLevels(StormDuration stormDuration) {
        this.stormDuration = stormDuration;
    }


    /**
     * Returns the impacts for this affected area at this storm severity (which is the calculated max)
     * @param affectedBlock sq km affected
     * @param stormSeverity max value for the intensity of the stor
     * @return a map of impact type to value, empty if none
     */
    public EnumMap<ImpactType, Short> getImpacts(Integer affectedBlock, StormSeverity stormSeverity) {
        EnumMap<ImpactType, Short> consequencesForThisBlock = new EnumMap<>(ImpactType.class);
        EnumMap<ImpactType, Map<Integer, Short>> levelsPerSeverity = impactLevels.getOrDefault(stormSeverity,
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


    /**
     * Returns the impacts of the threshold-breaking block of data
     * Removes a block if it has no consequences
     *
     * @param affectedBlock the blcok in question
     * @return a map of what it means for this block, for each storm severity and each impact type
     */
    public EnumMap<StormSeverity, EnumMap<ImpactType, Short>> getValuesPerImpactType(Integer affectedBlock) {
        EnumMap<StormSeverity, EnumMap<ImpactType, Short>> impactsForBlock = new EnumMap<>(StormSeverity.class);
        for (StormSeverity stormSeverity : StormSeverity.values()) {
            EnumMap<ImpactType, Short> consequencesForThisBlock = new EnumMap<>(ImpactType.class);
            EnumMap<ImpactType, Map<Integer, Short>> levelsPerSeverity = impactLevels.getOrDefault(stormSeverity,
                    new EnumMap<>(ImpactType.class));
            for (ImpactType impactType : ImpactType.values()) {
                short impactForThisBlock = levelsPerSeverity.getOrDefault(impactType, new HashMap<>())
                        .getOrDefault(affectedBlock, ZERO);
                if (impactForThisBlock > ZERO) {
                    consequencesForThisBlock.put(impactType, impactForThisBlock);
                }
            }
            if (!consequencesForThisBlock.isEmpty()) {
                impactsForBlock.put(stormSeverity, consequencesForThisBlock);
            }
        }
        return impactsForBlock;
    }

//    private EnumMap<ImpactType, Short> blockConsequences(Integer block) {
//        EnumMap<ImpactType, Short> consequencesForThisBlock = new EnumMap<>(ImpactType.class);
//        for (ImpactType impactType : ImpactType.values()) {
//            short impactForThisBlock = impactLevels.computeIfAbsent(impactType, i -> new HashMap<>())
//                    .getOrDefault(block, ZERO);
//            if (impactForThisBlock > 0) {
//                consequencesForThisBlock.put(impactType, impactForThisBlock);
//            }
//        }
//        return consequencesForThisBlock;
//    }

    public void put(StormSeverity stormSeverity, ImpactType impact, Map<Integer, Short> thresholdValues) {
        impactLevels.computeIfAbsent(stormSeverity, s -> new EnumMap<>(ImpactType.class))
                .put(impact, thresholdValues);
    }

    public EnumMap<StormSeverity, EnumMap<ImpactType, Map<Integer, Short>>> getImpactLevels() {
        return impactLevels;
    }

    public StormDuration getStormDuration() {
        return stormDuration;
    }

}
