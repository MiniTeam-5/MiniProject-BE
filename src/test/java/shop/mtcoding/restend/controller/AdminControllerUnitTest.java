package shop.mtcoding.restend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import shop.mtcoding.restend.core.advice.MyLogAdvice;
import shop.mtcoding.restend.core.advice.MyValidAdvice;
import shop.mtcoding.restend.core.annotation.MyErrorLog;
import shop.mtcoding.restend.core.annotation.MyLog;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.config.MyFilterRegisterConfig;
import shop.mtcoding.restend.core.config.MySecurityConfig;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.manage.Manage;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.core.MyWithMockUser;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@ActiveProfiles("test")
@EnableAspectJAutoProxy // AOP 활성화
@Import({
        MyValidAdvice.class,
        MyLogAdvice.class,
        MySecurityConfig.class,
        MyFilterRegisterConfig.class
}) // Advice 와 Security 설정 가져오기
@WebMvcTest(
        // 필요한 Controller 가져오기, 특정 필터를 제외하기
        controllers = {AdminController.class}
)
public class AdminControllerUnitTest extends DummyEntity{


    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private  UserRepository userRepository;

    @Autowired
    private ObjectMapper om;


    @MyWithMockUser(id = 1L, username = "cos", role = "ADMIN", fullName = "코스")
    @Test
    public void annualUpdate_test() throws Exception{
        // given
        Long id = 2L;
        Manage.AnnualRequestDTO annualRequestDTO = new Manage.AnnualRequestDTO(5);
        String requestBody = om.writeValueAsString(annualRequestDTO);


        User ssar = newMockUser(2L,"sockja","숙자","USER",5);
        Manage manage = new Manage().toEntityOut(ssar);
        Mockito.when(userService.연차수정(Mockito.any(Long.class), any(Manage.AnnualRequestDTO.class)))
                .thenReturn(manage);

        //.with(SecurityMockMvcRequestPostProcessors.user("cos").roles("ADMIN"))

        //when
        ResultActions resultActions = mvc
                .perform(post("/auth/admin/annual/"+id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.data.userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.remain_days").value(5));
        resultActions.andExpect(status().isOk());
    }


    @MyWithMockUser(id = 9L, username = "cos", role = "ADMIN", fullName = "코스")
    @Test
    public void userChart_test() throws Exception {
        String img = "img";
        // Given
        Manage.UserManageDTO userManageDTO = new Manage.UserManageDTO();
        List<Manage.UserManageDTO> userList = new ArrayList<>();
        Manage.UserManageDTO userPS1 = newMockChartUser(1L,"USER","gamja", LocalDateTime.of(2023, 5, 10, 12, 30),2,img);
        Manage.UserManageDTO userPS2 = newMockChartUser(2L,"USER","suckja", LocalDateTime.of(2023, 5, 10, 12, 30),2,img);
        Manage.UserManageDTO userPS3 = newMockChartUser(3L,"USER","nana", LocalDateTime.of(2023, 5, 10, 12, 30),2,img);
        Manage.UserManageDTO userPS4 = newMockChartUser(4L,"USER","po", LocalDateTime.of(2023, 5, 10, 12, 30),2,img);
        Manage.UserManageDTO userPS5 = newMockChartUser(5L,"USER","bora", LocalDateTime.of(2023, 5, 10, 12, 30),2,img);
        Manage.UserManageDTO userPS6 = newMockChartUser(6L,"USER","zelda", LocalDateTime.of(2023, 5, 10, 12, 30),2,img);
        Manage.UserManageDTO userPS7 = newMockChartUser(7L,"USER","link", LocalDateTime.of(2023, 5, 10, 12, 30),2,img);
        Manage.UserManageDTO userPS8 = newMockChartUser(8L,"USER","ribal", LocalDateTime.of(2023, 5, 10, 12, 30),2,img);

        //        Pageable pageable = PageRequest.of(pageValue, sizeValue);
        //        Page<Manage.UserManageDTO> pageSizePG = new PageImpl<>(Arrays.asList(userPS1, userPS2, userPS3, userPS4, userPS5, userPS6, userPS7, userPS8), pageable, 8);

        Page<Manage.UserManageDTO> userListPG = new PageImpl<>(Arrays.asList(userPS1, userPS2, userPS3, userPS4, userPS5, userPS6, userPS7, userPS8));

        Mockito.when(userService.회원목록보기(any())).thenReturn(userListPG);

        // when
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/auth/admin")
                        .param("page", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        int pageValue = Integer.parseInt(mvcResult.getRequest().getParameter("page"));
        int sizeValue = Integer.parseInt(mvcResult.getRequest().getParameter("size"));

        System.out.println(pageValue+ " " + sizeValue);



        // Then
        String responseJson = mvcResult.getResponse().getContentAsString();
        JSONObject responseObject = new JSONObject(responseJson);
        JSONArray contentArray = responseObject.getJSONObject("data").getJSONArray("content");
        int contentArraySize = contentArray.length();

        assertEquals(0,Integer.parseInt(mvcResult.getRequest().getParameter("page")));
        assertEquals(3,Integer.parseInt(mvcResult.getRequest().getParameter("size")));
        assertEquals(1L, contentArray.getJSONObject(0).getLong("userId"));
        assertEquals("USER", contentArray.getJSONObject(0).getString("role"));
        assertEquals("gamja", contentArray.getJSONObject(0).getString("username"));
        assertEquals("2023-05-10T12:30:00", contentArray.getJSONObject(0).getString("hire_date"));
        assertEquals(2L, contentArray.getJSONObject(1).getLong("userId"));
        assertEquals("USER", contentArray.getJSONObject(1).getString("role"));
        assertEquals("suckja", contentArray.getJSONObject(1).getString("username"));
        assertEquals("2023-05-10T12:30:00", contentArray.getJSONObject(1).getString("hire_date"));

    }

    @MyWithMockUser(id = 2L, username = "cos", role = "MASTER", fullName = "코스")
    @Test
    public void roleUpdate_test() throws Exception{
        // given
        Long id = 1L;
        Manage.MasterInDTO masterIn = new Manage.MasterInDTO();
        masterIn.setRole("ADMIN");
        String requestBody = om.writeValueAsString(masterIn);

        // stub
        User ssar = newMockUser(1L,"sockja","숙자","ADMIN",5);
        Manage.MasterOutDTO masterOutDTO = new Manage.MasterOutDTO().toEntityOut(ssar);
        Mockito.when(userService.권한수정(any(Long.class), any(Manage.MasterInDTO.class))).thenReturn(masterOutDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/auth/master/"+id)
                                .content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.userId").value(1L));
        resultActions.andExpect(jsonPath("$.data.role").value("ADMIN"));
        resultActions.andExpect(status().isOk());
    }
}
