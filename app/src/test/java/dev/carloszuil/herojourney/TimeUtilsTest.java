package dev.carloszuil.herojourney;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dev.carloszuil.herojourney.util.TimeUtils;

public class TimeUtilsTest {

    @Test
    public void testFormatMillisToTimer_0Sec_returns0000() {
        String result = TimeUtils.formatMillisToTimer(0L);
        assertEquals("00:00", result);
    }

    @Test
    public void testFormatMillisToTimer_5sec_returns0005() {
        String result = TimeUtils.formatMillisToTimer(5_000L);
        assertEquals("00:05", result);
    }

    @Test
    public void testFormatMillisToTimer_1Minute_returns0100() {
        String result = TimeUtils.formatMillisToTimer(60_000L);
        assertEquals("01:00", result);
    }

    @Test
    public void testFormatMillisToTimer_90sec_returns0130() {
        String result = TimeUtils.formatMillisToTimer(90_000L);
        assertEquals("01:30", result);
    }

    @Test
    public void testFormatMillisToTimer_negative_returns0000() {
        String result = TimeUtils.formatMillisToTimer(-10L);
        assertEquals("00:00", result);
    }

    @Test
    public void testCalculateRemaining_timeNotReached_returnsRemaining() {
        long start = 1_000L;
        long now = 4_000L;
        long duration = 10_000L;
        long remaining = TimeUtils.calculateRemaining(start, now, duration);
        assertEquals(7_000L, remaining);
    }

    @Test
    public void testCalculateRemaining_timeReached_returns0() {
        long start = 0L;
        long now = 20_000L;
        long duration = 10_000L;
        long remaining = TimeUtils.calculateRemaining(start, now, duration);
        assertEquals(0L, remaining);
    }

    @Test
    public void testCalculateRemaining_timeExactlyReached_returns0() {
        long start = 0L;
        long now = 10_000L;
        long duration = 10_000L;
        long remaining = TimeUtils.calculateRemaining(start, now, duration);
        assertEquals(0L, remaining);
    }

}
