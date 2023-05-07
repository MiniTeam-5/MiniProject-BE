package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class MyDateService {

    private final MyHolidayService myHolidayService;

    public Integer getWeekDayCount(LocalDate startDate, LocalDate endDate) throws URISyntaxException {
        Integer weekdayCount = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                // if 공휴일이면 continue;
                String year = String.valueOf(date.getYear());
                String month = String.format("%02d", date.getMonthValue());
                Set<LocalDate> holidays = myHolidayService.getHolidays(year, month);
                if(holidays.contains(date)) continue;
                weekdayCount++;
            }
        }
        return weekdayCount;
    }
}
