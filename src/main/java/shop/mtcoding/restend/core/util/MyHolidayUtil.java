package shop.mtcoding.restend.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class MyHolidayUtil {
    public static Set<LocalDate> getHolidays(String year, String month) throws URISyntaxException {
        // API로 공휴일 값 받기
        RestTemplate restTemplate = new RestTemplate();
        String key = "aMXansqXuFSDG%2BmXY5GMZcqfIlSUT6DfjOAA4PBOU8i8cBJW6m8lZ1PetyrpIUmz%2Bo3qBUHReXaIaVAXZ3De6g%3D%3D";
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
