package utils;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {

    public static Map<String, String> getDates() {
        Map<String, String> dates = new HashMap<>();

        DateTimeFormatter zonedFormatter = new DateTimeFormatterBuilder()
            .appendInstant(3)
            .toFormatter();

        ZonedDateTime startDate = ZonedDateTime.now(ZoneOffset.UTC)
            .plusDays(1)
            .withHour(8)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        ZonedDateTime expirationDate = startDate.plusDays(5);

        dates.put("startDate", startDate.format(zonedFormatter));
        dates.put("expirationDate", expirationDate.format(zonedFormatter));

        return dates;
    }
}
