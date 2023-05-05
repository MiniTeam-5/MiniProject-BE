package shop.mtcoding.restend.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
class MyHolidayUtilTest {
    @InjectMocks
    private MyHolidayUtil myHolidayUtil;

    @Test
    void getHolidays_test() throws URISyntaxException {

        Set<LocalDate> actualResponse = myHolidayUtil.getHolidays("2023", "05");
        System.out.println("테스트 : ");
        for (LocalDate item : actualResponse) {
            System.out.println(item);
        }
        //Set{2023-05-29, 2023-05-27, 2023-05-05}
    }
}
