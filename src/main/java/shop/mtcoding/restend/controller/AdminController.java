package shop.mtcoding.restend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception403;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.manage.Manage;
import shop.mtcoding.restend.service.UserService;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final UserService userService;
    private final HttpSession session;

    // 회원 정보 변경 로직
    @PostMapping("/auth/admin/{id}/update")
    public ResponseEntity<?> annualUpdate(@PathVariable Long id, @RequestBody Manage manage, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        // 1. 권한 확인
        if ("MASTER".equals(myUserDetails.getUser().getRole()) || "ADMIN".equals(myUserDetails.getUser().getRole())) {
            // 2. 바꾸려는 회원정보가 있는지 확인 후 회원 정보 업데이트
            Manage managePS = userService.연차수정(id, manage);
            ResponseDTO<?>responseDTO = new ResponseDTO<>(HttpStatus.OK,"success",managePS);
            // 3. 변경된 정보를 앞단에 건네준다.
            //checkpoint : 받은 데이터를 그래도 돌려줘야하나? 아니면 User객체로 건네주어야하나
            // 아니면, 회원 관리 페이지를 통째로 줘야하나, 아니면 리다이렉션 해야되나..
            return ResponseEntity.ok().body(responseDTO);
        }throw new Exception403("권한이 없습니다.");
    }

    @GetMapping("/auth/admin/")
    public ResponseEntity<?> userChart(@RequestParam(defaultValue = "10") int page,@AuthenticationPrincipal MyUserDetails myUserDetails) {
        // 1. 권한 확인
        if ("MASTER".equals(myUserDetails.getUser().getRole()) || "ADMIN".equals(myUserDetails.getUser().getRole())) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Manage.UserManageDTO> userListPS = userService.회원목록보기(pageRequest);
        ResponseDTO<Page> responseDTO = new ResponseDTO<>(userListPS);
        return ResponseEntity.ok().body(responseDTO);
    }
        throw new Exception403("권한이 없습니다.");
    }

    // role까지 변경가능, master로 접근해야, role변경이 가능하다.
    @PostMapping("/auth/master/{id}/update")
    public ResponseEntity<?> userUpdate(@PathVariable Long id,@RequestBody Manage.MasterDTO masterDTO, @AuthenticationPrincipal MyUserDetails myUserDetails){
        if(!"MASTER".equals(myUserDetails.getUser().getRole())){
            throw new Exception403("권한이 없습니다.");
        }
        Manage.MasterDTO masterOut = userService.권한수정(id,masterDTO);
        ResponseDTO<?>responseDTO = new ResponseDTO<>(HttpStatus.OK,"success",masterOut);
        return ResponseEntity.ok().body(responseDTO);
    }
}