package shop.mtcoding.restend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Service
public class HolidayService {
    private final String key;

    public HolidayService(@Value("${key.holiday}") String key) {
        this.key = key;
    }

    public Set<LocalDate> getHolidays(String year, String month) throws URISyntaxException {
        // API로 공휴일 값 받기
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?solYear="+year
                +"&solMonth="+month+"&_type=json&ServiceKey=";
        String response = restTemplate.getForObject(new URI(url+key), String.class);

        // 공휴일 찾아서 set에 저장
        Set<LocalDate> holidays = new HashSet<>();
        int idx = -17;
        while(true){
            idx = response.indexOf("locdate", idx + 17);
            if(idx == -1) break;
            String holiday = response.substring(idx + 9, idx + 17);
            holidays.add(LocalDate.parse(holiday, DateTimeFormatter.BASIC_ISO_DATE));
        }
        return holidays;
    }
}
