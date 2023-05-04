package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import shop.mtcoding.restend.core.MyRestDoc;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.manage.Manage;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.service.UserService;

import javax.persistence.EntityManager;

@DisplayName("사원 관리 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 10000)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AdminControllerUnitTest extends MyRestDoc {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository.save(dummy.newUser("ssar", "쌀"));
        userRepository.save(dummy.newUser("cos", "코스"));
        em.clear();
    }
    @DisplayName("연차 업데이트 성공")
    public void annualUpdate_test() throws Exception{
        // given
        Long id = 1L;
        Manage manage = new Manage();
        manage.setAnnual_count(5);

        // stub

        // when

        // then
    }
}
