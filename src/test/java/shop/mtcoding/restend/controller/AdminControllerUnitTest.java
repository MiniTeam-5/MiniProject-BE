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
import shop.mtcoding.restend.model.user.UserRole;
import shop.mtcoding.restend.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDate;
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
        MyFilterRegisterConfig.class,
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


    //@WithMockUser(roles = {"ADMIN"}) 이것도 된다.
    @MyWithMockUser(id = 1L, username = "cos", role = UserRole.ROLE_ADMIN)
    @Test
    public void annualUpdate_test() throws Exception{
        // given
        Long id = 2L;
        Manage.AnnualRequestDTO annualRequestDTO = new Manage.AnnualRequestDTO(5);
        String requestBody = om.writeValueAsString(annualRequestDTO);


        User ssar = newMockUser(2L,"sockja",5);
        Manage manage = new Manage().toEntityOut(ssar);
        Mockito.when(userService.연차수정(Mockito.any(Long.class), any(Manage.AnnualRequestDTO.class)))
                .thenReturn(manage);

        //.with(SecurityMockMvcRequestPostProcessors.user("cos").roles("ADMIN"))

        //when
        ResultActions resultActions = mvc
                .perform(post("/admin/annual/"+id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);


        //then
        resultActions.andExpect(jsonPath("$.data.userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.remain_days").value(5));
        resultActions.andExpect(status().isOk());
    }


    @MyWithMockUser(id = 10L, username = "zelda", role = UserRole.ROLE_ADMIN)
    @Test
    public void userChart_test() throws Exception {
        String img = "img";
        // Given
        Manage.UserManageDTO userManageDTO = new Manage.UserManageDTO();
        List<Manage.UserManageDTO> userList = new ArrayList<>();
        Manage.UserManageDTO userPS1 = newMockChartUser(1L, UserRole.ROLE_USER,"gamja", LocalDate.of(2023, 5, 10),2,img);
        Manage.UserManageDTO userPS2 = newMockChartUser(2L,UserRole.ROLE_USER,"suckja", LocalDate.of(2023, 5, 10),2,img);
        Manage.UserManageDTO userPS3 = newMockChartUser(3L,UserRole.ROLE_USER,"nana", LocalDate.of(2023, 5, 10),2,img);
        Manage.UserManageDTO userPS4 = newMockChartUser(4L,UserRole.ROLE_USER,"po", LocalDate.of(2023, 5, 10),2,img);
        Manage.UserManageDTO userPS5 = newMockChartUser(5L,UserRole.ROLE_USER,"bora", LocalDate.of(2023, 5, 10),2,img);
        Manage.UserManageDTO userPS6 = newMockChartUser(6L,UserRole.ROLE_USER,"zelda", LocalDate.of(2023, 5, 10),2,img);
        Manage.UserManageDTO userPS7 = newMockChartUser(7L,UserRole.ROLE_USER,"link", LocalDate.of(2023, 5, 10),2,img);
        Manage.UserManageDTO userPS8 = newMockChartUser(8L,UserRole.ROLE_USER,"ribal", LocalDate.of(2023, 5, 10),2,img);

        //        Pageable pageable = PageRequest.of(pageValue, sizeValue);
        //        Page<Manage.UserManageDTO> pageSizePG = new PageImpl<>(Arrays.asList(userPS1, userPS2, userPS3, userPS4, userPS5, userPS6, userPS7, userPS8), pageable, 8);

        Page<Manage.UserManageDTO> userListPG = new PageImpl<>(Arrays.asList(userPS1, userPS2, userPS3, userPS4, userPS5, userPS6, userPS7, userPS8));

        Mockito.when(userService.회원목록보기(any())).thenReturn(userListPG);

        // when
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/admin")
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
        assertEquals("ROLE_USER", contentArray.getJSONObject(0).getString("role"));
        assertEquals("gamja", contentArray.getJSONObject(0).getString("username"));
        assertEquals("2023-05-10", contentArray.getJSONObject(0).getString("hireDate"));
        assertEquals(2L, contentArray.getJSONObject(1).getLong("userId"));
        assertEquals("ROLE_USER", contentArray.getJSONObject(1).getString("role"));
        assertEquals("suckja", contentArray.getJSONObject(1).getString("username"));
        assertEquals("2023-05-10", contentArray.getJSONObject(1).getString("hireDate"));

    }

    @MyWithMockUser(id = 2L, username = "ssar", role = UserRole.ROLE_MASTER)
    @Test
    public void roleUpdate_test() throws Exception{
        // given
        Long id = 1L;
        Manage.MasterInDTO masterIn = new Manage.MasterInDTO();
        masterIn.setRole(UserRole.ROLE_ADMIN);
        String requestBody = om.writeValueAsString(masterIn);

        // stub
        User ssar = newMockUserRole(1L,"sockja",5,UserRole.ROLE_ADMIN);
        Manage.MasterOutDTO masterOutDTO = new Manage.MasterOutDTO().toEntityOut(ssar);
        Mockito.when(userService.권한수정(any(Long.class), any(Manage.MasterInDTO.class))).thenReturn(masterOutDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/master/"+id)
                                .content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.userId").value(1L));
        resultActions.andExpect(jsonPath("$.data.role").value("ROLE_ADMIN"));
        resultActions.andExpect(status().isOk());
    }
}
