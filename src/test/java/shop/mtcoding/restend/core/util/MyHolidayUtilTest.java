package shop.mtcoding.restend.core.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
class MyHolidayUtilTest {
    @InjectMocks
    private MyHolidayUtil myHolidayUtil;

    @Test
    void getHolidays_test() throws URISyntaxException {
        // given
        String year = "2023";
        String month = "05";
        Set<LocalDate> expected = new HashSet<>(Arrays.asList(LocalDate.parse("2023-05-29"),
                LocalDate.parse("2023-05-27"), LocalDate.parse("2023-05-05")));

        // when
        Set<LocalDate> actual = myHolidayUtil.getHolidays("2023", "05");

        // then
        Assertions.assertThat(expected).isEqualTo(expected);
    }
}
