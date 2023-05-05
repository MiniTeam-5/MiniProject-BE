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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void annualUpdate_test() throws Exception{
        // given
        Long id = 1L;
        Manage.AnnualRequestDTO annualRequestDTO = new Manage.AnnualRequestDTO(5);

        String requestBody = om.writeValueAsString(annualRequestDTO);

        //stub
        Manage manage = new Manage();
        User userPS = newMockUser(1L,"cos","코스","ADMIN",2);
        when(userService.연차수정(Mockito.any(Long.class), any(Manage.AnnualRequestDTO.class)))
                .thenReturn(manage.toEntityOut(userPS));

        // 설정
        //SecurityContextHolder.getContext().setAuthentication((Authentication) myUserDetails);

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/admin/annual/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(manage))
                        .with(SecurityMockMvcRequestPostProcessors.user(myUserDetails))
        );

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+ responseBody);

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.msg").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();
    }

    @MyWithMockUser(id = 1L, username = "cos", role = "ADMIN", fullName = "코스")
    public void userChart_test() throws Exception{
        // given
        int size = 5;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));


        // stub
        User userPS1 = newMockUser(1L,"gamja","감자","USER",2);
        User userPS2 = newMockUser(2L,"suckja","숙자","USER",2);
        User userPS3 = newMockUser(3L,"goguma","고구마","USER",2);
        User userPS4 = newMockUser(4L,"hama","하마","USER",2);
        User userPS5 = newMockUser(5L,"saja","사자","USER",2);
        User userPS6 = newMockUser(6L,"gogi","고기","USER",2);
        User userPS7 = newMockUser(7L,"dodosa","도도새","USER",2);
        User userPS8 = newMockUser(8L,"chicken","치킨","USER",2);
        List<User> userList = Arrays.asList(userPS1, userPS2, userPS3, userPS4, userPS5, userPS6, userPS7, userPS8);

        Manage.UserManageDTO userManageDTO = new Manage.UserManageDTO();
        Page<Manage.UserManageDTO> usersPG = new PageImpl<>(userList.stream().map(user -> userManageDTO.toEntityOut(user) ).collect(Collectors.toList()), pageRequest, userList.size());

        Mockito.when(userService.회원목록보기(any())).thenReturn(usersPG);

        //when
        ResultActions resultActions = mvc
                .perform(get("/admin?page=2&size=5"));


        // then

    }

}
