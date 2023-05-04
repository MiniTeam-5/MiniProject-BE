package shop.mtcoding.restend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import shop.mtcoding.restend.core.config.MyScheduleConfig;

@SpringBootApplication
@Import(MyScheduleConfig.class)
public class RestendApplication {

    public static void main(String[] args) {
        // Sentry.io 연결해서 !!
        SpringApplication.run(RestendApplication.class, args);
    }

}
