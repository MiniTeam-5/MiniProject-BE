package shop.mtcoding.restend.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import shop.mtcoding.restend.service.MyHolidayService;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
class MyHolidayServiceTest {
    @InjectMocks
    private MyHolidayService myHolidayService;

    @BeforeEach
    void setUp() {
//        myHolidayService = new MyHolidayService("키값직접넣어줘야함");
    }

    @Test
    void getHolidays_test() throws URISyntaxException {
        // given
        String year = "2023";
        String month = "05";
        Set<LocalDate> expected = new HashSet<>(Arrays.asList(LocalDate.parse("2023-05-29"),
                LocalDate.parse("2023-05-27"), LocalDate.parse("2023-05-05")));

        // when
//        Set<LocalDate> actual = myHolidayService.getHolidays("2023", "05");

        // then
//        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
