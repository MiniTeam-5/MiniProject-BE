package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class DateService {

    private final HolidayService holidayService;

    public Integer getWeekDayCount(LocalDate startDate, LocalDate endDate) throws URISyntaxException {
        Integer weekdayCount = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                // if 공휴일이면 continue;
                String year = String.valueOf(date.getYear());
                String month = String.format("%02d", date.getMonthValue());
                Set<LocalDate> holidays = holidayService.getHolidays(year, month);
                if(holidays.contains(date)) continue;
                weekdayCount++;
            }
        }
        return weekdayCount;
    }

    public int getAnnualLimit(LocalDate hireDate) {
        long days = ChronoUnit.DAYS.between(hireDate, LocalDate.now());
        int limit = calPlusLimit(days);

        return limit <= 25 ? limit : 25;
    }

    private int calPlusLimit(long days) {
        if (days < 365 * 3) {
            return 15;
        } else {
            int ceil = (int) Math.ceil((double) (days - 1095) / 730);
            return 15 + ceil;
        }
    }
}
