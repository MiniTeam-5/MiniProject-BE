package shop.mtcoding.restend.core.util;

import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class MyDateUtil {

    public static String toStringFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static Integer getWeekDayCount(LocalDate startDate, LocalDate endDate) throws URISyntaxException {
        Integer weekdayCount = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                // if 공휴일이면 continue;
                String year = String.valueOf(date.getYear());
                String month = String.format("%02d", date.getMonthValue());
                Set<LocalDate> holidays = MyHolidayUtil.getHolidays(year, month);
                if(holidays.contains(date)) continue;
                weekdayCount++;
            }
        }
        return weekdayCount;
    }
}
