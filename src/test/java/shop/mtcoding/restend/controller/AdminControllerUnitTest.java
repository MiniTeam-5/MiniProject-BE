package shop.mtcoding.restend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import org.springframework.test.web.servlet.ResultActions;
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
import shop.mtcoding.restend.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    @MyWithMockUser(id = 1L, username = "cos", role = "ADMIN", fullName = "코스")
    @Test
    void testUserChart() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(1, 2, Sort.by("id").descending());
        List<User> userList = Arrays.asList(
                newMockUser(1L, "gamja", "감자", "USER", 2),
                newMockUser(2L, "suckja", "숙자", "USER", 2),
                newMockUser(3L, "goguma", "고구마", "USER", 2)
        );
        Page<Manage.UserManageDTO> userListPage = new PageImpl<>(userList.stream().map(user -> new Manage.UserManageDTO().toEntityOut(user)).collect(Collectors.toList()), pageRequest, userList.size());
        Mockito.when(userService.회원목록보기(pageRequest)).thenReturn(userListPage);

        // when
        ResultActions resultActions = mvc
                .perform(get("/auth/admin").param("page", "1").param("size", "2"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.data").exists());
        resultActions.andExpect(jsonPath("$.data.content").isArray());
        resultActions.andExpect(jsonPath("$.data.content.length()").value(userList.size()));
        resultActions.andExpect(jsonPath("$.data.totalPages").value(1));
        resultActions.andExpect(jsonPath("$.data.pageable.pageSize").value(20));
        resultActions.andExpect(jsonPath("$.data.pageable.pageNumber").value(5));
        resultActions.andReturn();
    }


}
