package kr.co.lupintech.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.lupintech.core.annotation.MyErrorLog;
import kr.co.lupintech.core.annotation.MyLog;
import kr.co.lupintech.core.auth.session.MyUserDetails;
import kr.co.lupintech.dto.PageDTO;
import kr.co.lupintech.dto.leave.LeaveResponse;
import kr.co.lupintech.dto.manage.ManageResponse;
import kr.co.lupintech.dto.manage.ManageUserDTO;
import kr.co.lupintech.dto.manage.ResponsePagenation;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.service.LeaveService;
import kr.co.lupintech.service.ManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import kr.co.lupintech.dto.ResponseDTO;
import kr.co.lupintech.model.leave.enums.LeaveStatus;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class AdminController {


    private final ManageService manageService;

    private final LeaveService leaveService;

    // 회원 정보 변경 로직
    @PostMapping("/admin/annual/{id}")
    public ResponseEntity<?> annualUpdate(@PathVariable Long id, @RequestBody @Valid ManageUserDTO.AnnualRequestDTO annualRequestDTO) throws JsonProcessingException {

        // 1. 바꾸려는 회원정보가 있는지 확인 후 회원 정보 업데이트
            ManageUserDTO manageUserDTOPS = manageService.연차수정(id, annualRequestDTO);
            ResponseDTO<?>responseDTO = new ResponseDTO<>(manageUserDTOPS);

            return ResponseEntity.ok().body(responseDTO);

    }

    @GetMapping("/admin/leave")
    public ResponseEntity<?> getLeave() {
        List<LeaveResponse.InfoOutDTO> waitingLeaves = leaveService.상태선택연차당직정보가져오기(LeaveStatus.WAITING);
        ResponseDTO<List<LeaveResponse.InfoOutDTO>> responseDTO = new ResponseDTO<>(waitingLeaves);
        return ResponseEntity.ok().body(responseDTO);
    }

    // role까지 변경가능, master로 접근해야, role변경이 가능하다.
    @PostMapping("/master/{id}")
    public ResponseEntity<?> roleUpdate(@PathVariable Long id,@RequestBody ManageUserDTO.MasterInDTO masterIn,
                                        @AuthenticationPrincipal MyUserDetails myUserDetails){

        ManageUserDTO.MasterOutDTO masterOut = manageService.권한수정(id,masterIn);
        ResponseDTO<?>responseDTO = new ResponseDTO<>(HttpStatus.OK,"success",masterOut);
        return ResponseEntity.ok().body(responseDTO);
    }

    // 사원 목록 조회 + 검색
    @MyLog
    @MyErrorLog
    @GetMapping("/admin") // /admin?query=김&page=0&size=10
    public ResponseEntity<?> search(@RequestParam(defaultValue = "") String query,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        PageDTO<ManageResponse.UserOutDTO, User> pageDTO = manageService.사원검색(query, pageable);
        ResponseDTO<PageDTO<ManageResponse.UserOutDTO, User>> responseDTO = new ResponseDTO<>(pageDTO);
        return ResponseEntity.ok(responseDTO);
    }

}