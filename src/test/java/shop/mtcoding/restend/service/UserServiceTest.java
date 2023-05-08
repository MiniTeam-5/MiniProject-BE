package shop.mtcoding.restend.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.dummy.DummyEntity;
import shop.mtcoding.restend.dto.manage.Manage;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
// @SpringBootTest 어노테이션을 붙이게되면 JUnit5의 Mockito기능이 없어지고, 의존성 주입을 하게 된다.
public class UserServiceTest extends DummyEntity {

    // 진짜 userService 객체를 만들고 Mock로 Load된 모든 객체를 userService에 주입
    @InjectMocks
    private UserService userService;

    // 진짜 객체를 만들어서 Mockito 환경에 Load
    @Mock
    private UserRepository userRepository;

    // 가짜 객체를 만들어서 Mockito 환경에 Load
    @Mock
    private AuthenticationManager authenticationManager;

    // 진짜 객체를 만들어서 Mockito 환경에 Load
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Test
    public void hello_test() {
        String pw = "1234";
        String enc = bCryptPasswordEncoder.encode(pw);
        System.out.println(enc);
    }

    @Test
    public void 회원가입_test() throws Exception {

        // given
        UserRequest.JoinInDTO joinInDTO = new UserRequest.JoinInDTO();
        joinInDTO.setUsername("cos");
        joinInDTO.setPassword("1234");
        joinInDTO.setEmail("cos@nate.com");
        joinInDTO.setHireDate("2022-12-12");

        // stub 1
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        // stub 2
        User cos = newMockUser(1L, "cos", 15);
        Mockito.when(userRepository.save(any())).thenReturn(cos);

        // when
        UserResponse.JoinOutDTO joinOutDTO = userService.회원가입(joinInDTO);

        // then
        Assertions.assertThat(joinOutDTO.getId()).isEqualTo(1L);
        Assertions.assertThat(joinOutDTO.getUsername()).isEqualTo("cos");
    }

    @Test
    public void 로그인_test() throws Exception {
        // given
        UserRequest.LoginInDTO loginInDTO = new UserRequest.LoginInDTO();
        loginInDTO.setEmail("abcd@nate.com");
        loginInDTO.setPassword("1234");

        // stub


        User cos = newMockUser(1L, "cos", 15);
        MyUserDetails myUserDetails = new MyUserDetails(cos);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                myUserDetails, myUserDetails.getPassword(), myUserDetails.getAuthorities()
        );
        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // when
        String jwt = userService.로그인(loginInDTO);
        System.out.println("디버그 : " + jwt);

        // then
        Assertions.assertThat(jwt.startsWith("Bearer ")).isTrue();
    }

    @Test
    public void 유저상세보기_test() throws Exception {
        // given
        Long id = 1L;

        // stub


        User cos = newMockUser(1L, "cos", 15);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        // when
        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(id);

        // then
        Assertions.assertThat(detailOutDTO.getId()).isEqualTo(1L);
        Assertions.assertThat(detailOutDTO.getUsername()).isEqualTo("cos");
        Assertions.assertThat(detailOutDTO.getEmail()).isEqualTo("cos@nate.com");
        Assertions.assertThat(detailOutDTO.getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    public void 연차수정_test() throws Exception {
        //given
        Long id = 1L;
        Manage.AnnualRequestDTO annualRequestDTO = new Manage.AnnualRequestDTO(5);

        //stub
        User cos = newMockUser(1L, "cos",2); //Annual_limit = 2
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        //when
        Manage managePS = userService.연차수정(id, annualRequestDTO);

        //then
        Assertions.assertThat(managePS.getUserId().equals(1L));
        Assertions.assertThat(managePS.getRemain_days().equals(5));
    }

    @Test
    public void 권한수정_test() throws Exception {
        //given
        Long id = 1L;
        Manage.MasterInDTO masterIn = new Manage.MasterInDTO(UserRole.ROLE_ADMIN);
        //stub
        User cos = newMockUser(1L, "cos",2); // role = USER
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        //when
        Manage.MasterOutDTO masterPS = userService.권한수정(id, masterIn);

        //then
        Assertions.assertThat(masterPS.getUserId().equals(1L));
        Assertions.assertThat(masterPS.getRole().equals("ADMIN"));
    }


    @Test
    void 회원목록보기_test() {
        // given
        Manage.UserManageDTO userManageDTO = new Manage.UserManageDTO();
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 3, Sort.by("id").descending());

        // stub
        User userPS1 = newMockUser(1L,"gamja",2);
        User userPS2 = newMockUser(2L,"suckja",2);
        User userPS3 = newMockUser(3L,"goguma",2);
        User userPS4 = newMockUser(4L,"hama",2);
        User userPS5 = newMockUser(5L,"saja",2);


        List<User>userList = new ArrayList<>();
        userList.addAll(Arrays.asList(userPS1,userPS2,userPS3,userPS4,userPS5));

        Mockito.when(userRepository.findAll()).thenReturn(userList);

        // when
        Page<Manage.UserManageDTO>usersPG = userService.회원목록보기(pageRequest);
        String responsBody = usersPG.getContent().toString();
        System.out.println("Test : "+ responsBody);

        // then
        Assertions.assertThat(usersPG).isInstanceOf(Page.class);
        assertEquals(3, usersPG.getSize());

        Assertions.assertThat(usersPG.getContent().get(0).getUserId()).isEqualTo(1L);
        Assertions.assertThat(usersPG.getContent().get(0).getUsername()).isEqualTo("gamja");
        Assertions.assertThat(usersPG.getContent().get(0).getRole()).isEqualTo(UserRole.ROLE_USER);
        Assertions.assertThat(usersPG.getContent().get(0).getRemain_days()).isEqualTo(2);

    }

}
