package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.MyWithMockUser;
import shop.mtcoding.restend.core.advice.MyLogAdvice;
import shop.mtcoding.restend.core.advice.MyValidAdvice;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.config.MyFilterRegisterConfig;
import shop.mtcoding.restend.core.config.MySecurityConfig;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.manage.Manage;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.service.UserService;
import org.springframework.security.test.context.support.WithMockUser;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@WebMvcTest(controllers = AdminController.class)
@Import({MySecurityConfig.class, MyFilterRegisterConfig.class})
public class AdminControllerUnitTest extends DummyEntity{


    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper om = new ObjectMapper();

    User user = newMockUser(1L,"cos", "코스","ADMIN",2);
    private MyUserDetails myUserDetails = new MyUserDetails(user);

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAnnualUpdate() throws Exception{
        // given
        Long id = 1L;
        User userPS = User.builder().id(id).remain_days(5).build();

        Manage manage = new Manage().toEntityOut(userPS);

        when(userService.연차수정(Mockito.any(Long.class), any(Manage.AnnualRequestDTO.class)))
                .thenReturn(manage);

        // 설정
        SecurityContextHolder.getContext().setAuthentication((Authentication) myUserDetails);


        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/admin/annual/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
               // .content(om.writeValueAsString(manage))
               // .with(SecurityMockMvcRequestPostProcessors.user(myUserDetails))
        );

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+ responseBody);

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.msg").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();
    }

}
