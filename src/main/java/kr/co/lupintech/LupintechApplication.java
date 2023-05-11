package kr.co.lupintech;

import kr.co.lupintech.core.config.MyScheduleConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(MyScheduleConfig.class)
public class LupintechApplication {

    public static void main(String[] args) {
        // Sentry.io 연결해서 !!
        SpringApplication.run(LupintechApplication.class, args);
    }

}
