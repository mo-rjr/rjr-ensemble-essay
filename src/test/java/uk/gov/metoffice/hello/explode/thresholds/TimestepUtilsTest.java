package uk.gov.metoffice.hello.explode.thresholds;

import org.junit.Test;

import java.time.ZonedDateTime;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TimestepUtilsTest {

    @Test
    public void timestepBelongsInAccumulations() {
        // arrange
        /// one too early
        // one too late
        // one just right
        // and also two at either side of each boundary


        // act
        // TODO implement

        // assert
    }

    @Test
    public void relevantHourlyTimesteps() {
        // arrange
        // one on the hour
        // one not on the hour
        // one on the hour but near end
        // one not on the hour and near end


        // act
        // TODO implement

        // assert
    }

    @Test
    public void onTheHour() {
        // arrange
        ZonedDateTime onTheHour =  ZonedDateTime.parse("2007-12-03T11:00:00Z");
        ZonedDateTime notOnTheHour = ZonedDateTime.parse("2007-12-03T10:15:30Z");

        // act and assert
        assertTrue(TimestepUtils.onTheHour(onTheHour));
        assertFalse(TimestepUtils.onTheHour(notOnTheHour));
    }
}