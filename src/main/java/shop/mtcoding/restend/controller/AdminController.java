package shop.mtcoding.restend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.restend.core.annotation.MyErrorLog;
import shop.mtcoding.restend.core.annotation.MyLog;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception403;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.manage.Manage;
import shop.mtcoding.restend.service.UserService;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final UserService userService;

    // 회원 정보 변경 로직
    @PostMapping("/auth/admin/annual/{id}")
    public ResponseEntity<?> annualUpdate(@PathVariable Long id, @RequestBody @Valid Manage.AnnualRequestDTO annualRequestDTO) throws JsonProcessingException {
        // 1. 권한 확인  이미 Filter에서 권한 확인 마침. 이상하게,테스트 환경에서 myUserDetails에 객체 권한이 USER이다.

        // 2. 바꾸려는 회원정보가 있는지 확인 후 회원 정보 업데이트
            Manage managePS = userService.연차수정(id, annualRequestDTO);
            ResponseDTO<?>responseDTO = new ResponseDTO<>(managePS);
            // 3. 변경된 정보를 앞단에 건네준다.
            //checkpoint : 받은 데이터를 그래도 돌려줘야하나? 아니면 User객체로 건네주어야하나
            // 아니면, 회원 관리 페이지를 통째로 줘야하나, 아니면 리다이렉션 해야되나..
            return ResponseEntity.ok().body(responseDTO);

    }

    @GetMapping("/auth/admin") //  /admin?page=5&size=20
    public ResponseEntity<?> userChart(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        // 1. 권한 확인
        //if ("MASTER".equals(myUserDetails.getUser().getRole()) || "ADMIN".equals(myUserDetails.getUser().getRole())) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Manage.UserManageDTO> userListPS = userService.회원목록보기(pageRequest);
        ResponseDTO<Page> responseDTO = new ResponseDTO<>(userListPS);
        return ResponseEntity.ok().body(responseDTO);
    }

    // role까지 변경가능, master로 접근해야, role변경이 가능하다.
    @PostMapping("/master/role/{id}")
    public ResponseEntity<?> roleUpdate(@PathVariable Long id,@RequestBody Manage.MasterDTO masterDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
        if(!"MASTER".equals(myUserDetails.getUser().getRole())){
            throw new Exception403("권한이 없습니다.");
        }
        Manage.MasterDTO masterOut = userService.권한수정(id,masterDTO);
        ResponseDTO<?>responseDTO = new ResponseDTO<>(HttpStatus.OK,"success",masterOut);
        return ResponseEntity.ok().body(responseDTO);
    }
}