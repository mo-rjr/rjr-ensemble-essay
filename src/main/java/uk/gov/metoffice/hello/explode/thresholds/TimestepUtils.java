package uk.gov.metoffice.hello.explode.thresholds;

import uk.gov.metoffice.hello.domain.StormReturnPeriod;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Utility class for timestep-related calculations
 * Used when summing and maxxing across timesteps
 * -- and also {this}
 */
public class TimestepUtils {

    private TimestepUtils() {}

    /**
     * Method to work out whether it's worth calculating an accumulated value for this timestep
     * Some data is dropped from the beginning and end of each list of times in the data
     *
     * @param thisTimestepIndex where we are in the ordered list of times
     * @param accumulationSteps the number of steps we're summing over;
     *                          if we're summing over 4 steps, the first 3 won't have enough data;
     *                          they will have to be dropped anyway so no point doing anything about them
     * @param lastHourlyTimezoneIndex only data up to and including this index will be returned,
     *                                so there's no point calculating anything beyond here
     * @return whether to continue working on an accumulation sum for this timestep index
     */
    public static boolean timestepBelongsInAccumulations(int thisTimestepIndex,
                                                         int accumulationSteps,
                                                         int lastHourlyTimezoneIndex) {
        return thisTimestepIndex >= accumulationSteps - 1
                && thisTimestepIndex <= lastHourlyTimezoneIndex;
    }

    /**
     * For a given time, returns any hourly times which need its value as part of their threshold calculation
     *
     * The past hour for purposes of thresholding includes five 15-minute values not four,
     * e.g. the 6pm value is the max of 5pm, 5.15, 5.30, 5.45, 6pm
     * not just 5.15, 5.30, 5.45, 6pm
     *
     * @param currentZonedDateTime  this current timestep, e.g. 5pm or 5.15
     * @param hourlyStormReturnPeriods the accumulating treemap, the keys of which are only the hourly timesteps, e.g. 5pm, 6pm
     * @return all hourly timesteps to which this pertains
     * e.g. 5pm pertains to 5pm and 6pm,
     * 6.15 pertains to 7pm
     * and if the data only goes out to 8.45 then 8.30 pertains to no hourly timesteps
     */
    public static List<ZonedDateTime> relevantHourlyTimesteps(ZonedDateTime currentZonedDateTime,
                                                        TreeMap<ZonedDateTime, TreeMap<Integer, StormReturnPeriod>> hourlyStormReturnPeriods) {
        List<ZonedDateTime> relevantHourlyTimesteps = new ArrayList<>();

        if (onTheHour(currentZonedDateTime)) {
            relevantHourlyTimesteps.add(currentZonedDateTime);
        }

        ZonedDateTime nextHourlyValue = hourlyStormReturnPeriods.higherKey(currentZonedDateTime);
        if (nextHourlyValue != null) {
            relevantHourlyTimesteps.add(nextHourlyValue);
        }
        return relevantHourlyTimesteps;
    }

    /**
     * Is this time on the hour?  E.g. 7pm, rather than 7.15
     * @param zonedDateTime the time to check
     * @return whether it is on the hour
     */
    public static boolean onTheHour(ZonedDateTime zonedDateTime) {
        return zonedDateTime.getMinute() == 0;
    }
}
