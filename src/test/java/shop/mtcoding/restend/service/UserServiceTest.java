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

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        joinInDTO.setFullName("코스");

        // stub 1
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        // stub 2
        User cos = newMockUser(1L, "cos", "코스","USER",2);
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
        loginInDTO.setUsername("cos");
        loginInDTO.setPassword("1234");

        // stub
        User cos = newMockUser(1L, "cos", "코스","USER",2);
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
        User cos = newMockUser(1L, "cos", "코스","USER",2);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        // when
        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(id);

        // then
        Assertions.assertThat(detailOutDTO.getId()).isEqualTo(1L);
        Assertions.assertThat(detailOutDTO.getUsername()).isEqualTo("cos");
        Assertions.assertThat(detailOutDTO.getEmail()).isEqualTo("cos@nate.com");
        Assertions.assertThat(detailOutDTO.getFullName()).isEqualTo("코스");
        Assertions.assertThat(detailOutDTO.getRole()).isEqualTo("USER");
    }

    @Test
    public void 연차수정_test() throws Exception {
        //given
        Long id = 1L;
        Manage.AnnualRequestDTO annualRequestDTO = new Manage.AnnualRequestDTO(5);

        //stub
        User cos = newMockUser(1L, "cos", "코스모","USER",2); //Annual_limit = 2
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
        Manage.MasterDTO masterDTO = new Manage.MasterDTO(id, "ADMIN");
        //stub
        User cos = newMockUser(1L, "cos", "코스모","USER",2); // role = USER
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(cos));

        //when
        Manage.MasterDTO masterPS = userService.권한수정(id, masterDTO);

        //then
        Assertions.assertThat(masterPS.getUserId().equals(1L));
        Assertions.assertThat(masterPS.getRole().equals("ADMIN"));
    }


    @Test
    void 회원목록보기_test() {
        // given
        Manage.UserManageDTO userManageDTO = new Manage.UserManageDTO();
        int page = 10;
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("id").descending());

        // stub
        User userPS1 = newMockUser(1L,"gamja","감자","USER",2);
        User userPS2 = newMockUser(2L,"suckja","숙자","USER",2);
        User userPS3 = newMockUser(3L,"goguma","고구마","USER",2);
        User userPS4 = newMockUser(4L,"hama","하마","USER",2);
        User userPS5 = newMockUser(5L,"saja","사자","USER",2);


        List<User>userList = new ArrayList<>();
        userList.addAll(Arrays.asList(userPS1,userPS2,userPS3,userPS4,userPS5));

        Mockito.when(userRepository.findAll()).thenReturn(userList);
        // when
        List<User>userListPS = userRepository.findAll();
        Page<Manage.UserManageDTO> pagePS = new PageImpl<>(userListPS.stream().map(user -> userManageDTO.toEntityOut(user) ).collect(Collectors.toList()), pageRequest, userList.size());

        // then
        Assertions.assertThat(pagePS.getContent().get(0).getUserId()).isEqualTo(1L);
        Assertions.assertThat(pagePS.getContent().get(0).getUsername()).isEqualTo("gamja");
        Assertions.assertThat(pagePS.getContent().get(0).getRole()).isEqualTo("USER");
        Assertions.assertThat(pagePS.getContent().get(0).getRemain_days()).isEqualTo(2);

        Assertions.assertThat(pagePS).isInstanceOf(Page.class);
        Assertions.assertThat(pagePS.getContent()).hasSize(5);


    }

}
//        Assertions.assertThat(1L).isEqualTo(pagePS.getContent().get(0).getId());
//        Assertions.assertThat("gamja").isEqualTo(pagePS.getContent().get(0).getUsername());
//        Assertions.assertThat("USER").isEqualTo(pagePS.getContent().get(0).getRole());
//        Assertions.assertThat(2).isEqualTo(pagePS.getContent().get(0).getAnnual_count());