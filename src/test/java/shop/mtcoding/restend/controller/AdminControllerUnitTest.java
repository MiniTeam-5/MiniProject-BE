package shop.mtcoding.restend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import shop.mtcoding.restend.core.advice.MyLogAdvice;
import shop.mtcoding.restend.core.advice.MyValidAdvice;
import shop.mtcoding.restend.core.config.MyFilterRegisterConfig;
import shop.mtcoding.restend.core.config.MySecurityConfig;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.manage.ManageUserDTO;
import shop.mtcoding.restend.core.MyWithMockUser;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;
import shop.mtcoding.restend.service.ManageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    private ManageService manageService;

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
        ManageUserDTO.AnnualRequestDTO annualRequestDTO = new ManageUserDTO.AnnualRequestDTO(5);
        String requestBody = om.writeValueAsString(annualRequestDTO);


        User ssar = newMockUser(2L,"sockja",5);
        ManageUserDTO manageUserDTO = new ManageUserDTO().toEntityOut(ssar);
        Mockito.when(manageService.연차수정(Mockito.any(Long.class), any(ManageUserDTO.AnnualRequestDTO.class)))
                .thenReturn(manageUserDTO);

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
        resultActions.andExpect(jsonPath("$.data.remainDays").value(5));
        resultActions.andExpect(status().isOk());
    }


    @MyWithMockUser(id = 10L, username = "zelda", role = UserRole.ROLE_ADMIN)
    @Test
    public void userChart_test() throws Exception {

        // Given
        String img = "img";
        int expectedPageSize = 3;
        List<ManageUserDTO.ManageUserListDTO> userList = new ArrayList<>();
        ManageUserDTO.ManageUserListDTO userPS1 = newMockChartUser(1L, UserRole.ROLE_USER,"gamja", LocalDate.of(2023, 5, 10),2,img);
        ManageUserDTO.ManageUserListDTO userPS2 = newMockChartUser(2L,UserRole.ROLE_USER,"suckja", LocalDate.of(2023, 5, 10),2,img);
        ManageUserDTO.ManageUserListDTO userPS3 = newMockChartUser(3L,UserRole.ROLE_USER,"nana", LocalDate.of(2023, 5, 10),2,img);
        ManageUserDTO.ManageUserListDTO userPS4 = newMockChartUser(4L,UserRole.ROLE_USER,"po", LocalDate.of(2023, 5, 10),2,img);
        ManageUserDTO.ManageUserListDTO userPS5 = newMockChartUser(5L,UserRole.ROLE_USER,"bora", LocalDate.of(2023, 5, 10),2,img);
        ManageUserDTO.ManageUserListDTO userPS6 = newMockChartUser(6L,UserRole.ROLE_USER,"zelda", LocalDate.of(2023, 5, 10),2,img);
        ManageUserDTO.ManageUserListDTO userPS7 = newMockChartUser(7L,UserRole.ROLE_USER,"link", LocalDate.of(2023, 5, 10),2,img);
        ManageUserDTO.ManageUserListDTO userPS8 = newMockChartUser(8L,UserRole.ROLE_USER,"ribal", LocalDate.of(2023, 5, 10),2,img);


        Page<ManageUserDTO.ManageUserListDTO> userListPG = new PageImpl<>(Arrays.asList(userPS1, userPS2, userPS3, userPS4, userPS5, userPS6, userPS7, userPS8));

        Mockito.when(manageService.회원목록보기(any())).thenReturn(userListPG);

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


        String responseJson = mvcResult.getResponse().getContentAsString();
        JSONObject responseObject = new JSONObject(responseJson);
        JSONArray contentArray = responseObject.getJSONObject("data").getJSONArray("content");



        // Then
        assertEquals(expectedPageSize,responseObject.getInt("size"));
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
        ManageUserDTO.MasterInDTO masterIn = new ManageUserDTO.MasterInDTO();
        masterIn.setRole(UserRole.ROLE_ADMIN);
        String requestBody = om.writeValueAsString(masterIn);

        // stub
        User ssar = newMockUserRole(1L,"sockja",5,UserRole.ROLE_ADMIN);
        ManageUserDTO.MasterOutDTO masterOutDTO = new ManageUserDTO.MasterOutDTO().toEntityOut(ssar);
        Mockito.when(manageService.권한수정(any(Long.class), any(ManageUserDTO.MasterInDTO.class))).thenReturn(masterOutDTO);

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
