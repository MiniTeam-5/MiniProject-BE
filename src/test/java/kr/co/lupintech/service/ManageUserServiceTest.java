package kr.co.lupintech.service;

import kr.co.lupintech.dto.manage.ManageUserDTO;
import kr.co.lupintech.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import kr.co.lupintech.core.dummy.DummyEntity;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
// @SpringBootTest 어노테이션을 붙이게되면 JUnit5의 Mockito기능이 없어지고, 의존성 주입을 하게 된다.
public class ManageUserServiceTest extends DummyEntity {

    @InjectMocks
    private ManageService manageService;

    // 진짜 객체를 만들어서 Mockito 환경에 Load
    @Mock
    private UserRepository userRepository;


    @Test
    public void 연차수정_test() throws Exception {
        //given
        Long id = 1L;
        ManageUserDTO.AnnualRequestDTO annualRequestDTO = new ManageUserDTO.AnnualRequestDTO(5);

        //stub
        User cos = newMockUser(1L, "박코스","cos@nate.com",2); //Annual_limit = 2
        Mockito.when(userRepository.findByStatusAndId(true,id)).thenReturn(Optional.of(cos));

        //when
        ManageUserDTO manageUserDTOPS = manageService.연차수정(id, annualRequestDTO);

        //then
        Assertions.assertThat(manageUserDTOPS.getUserId().equals(1L));
        Assertions.assertThat(manageUserDTOPS.getRemainDays().equals(5));
    }

    @Test
    public void 권한수정_test() throws Exception {
        //given

        Long id = 1L;
        ManageUserDTO.MasterInDTO masterIn = new ManageUserDTO.MasterInDTO(UserRole.ROLE_ADMIN);
        //stub
        User expectedUser = newMockStateUser(1L, "박코스","cos@nate.com", 2,true); // role = USER


        Mockito.when(userRepository.findByStatusAndId(true, id)).thenReturn(Optional.of(expectedUser));

        //when
        ManageUserDTO.MasterOutDTO masterPS = manageService.권한수정(id, masterIn);

        //then
        Assertions.assertThat(masterPS.getUserId().equals(1L));
        Assertions.assertThat(masterPS.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
    }


    @Test
    void 회원목록보기_test() {
        // given
        ManageUserDTO.ManageUserListDTO manageUserListDTO = new ManageUserDTO.ManageUserListDTO();
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 3, Sort.by("id").descending());

        // stub
        User userPS1 = newMockUser(1L,"김감자", "gamja@nate.com", 2);
        User userPS2 = newMockUser(2L,"이숙자", "suckja@nate.com", 2);
        User userPS3 = newMockUser(3L,"고구마", "goguma@nate.com", 2);
        User userPS4 = newMockUser(4L,"이하마", "hama@nate.com", 2);
        User userPS5 = newMockUser(5L,"김사자","saja@nate.com",2);


        List<User> userList = new ArrayList<>();
        userList.addAll(Arrays.asList(userPS1,userPS2,userPS3,userPS4,userPS5));

        Mockito.when(userRepository.findAll()).thenReturn(userList);

        // when
        Page<ManageUserDTO.ManageUserListDTO> usersPG = manageService.회원목록보기(pageRequest);
        String responsBody = usersPG.getContent().toString();
        System.out.println("Test : "+ responsBody);

        // then
        Assertions.assertThat(usersPG).isInstanceOf(Page.class);
        assertEquals(3, usersPG.getSize());

        Assertions.assertThat(usersPG.getContent().get(0).getUserId()).isEqualTo(1L);
        Assertions.assertThat(usersPG.getContent().get(0).getUsername()).isEqualTo("김감자");
        Assertions.assertThat(usersPG.getContent().get(0).getRole()).isEqualTo(UserRole.ROLE_USER);
        Assertions.assertThat(usersPG.getContent().get(0).getRemainDays()).isEqualTo(2);

    }

}
