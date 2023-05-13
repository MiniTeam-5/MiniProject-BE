package kr.co.lupintech.controller;

import kr.co.lupintech.dto.leave.LeaveResponse;
import kr.co.lupintech.dto.manager.ManagerRequest;
import kr.co.lupintech.core.annotation.MyErrorLog;
import kr.co.lupintech.core.annotation.MyLog;
import kr.co.lupintech.dto.PageDTO;
import kr.co.lupintech.dto.manage.ManageResponse;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.service.LeaveService;
import kr.co.lupintech.service.ManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

    // 유저의 연차 일수 수정
    @PostMapping("/admin/annual/{id}")
    public ResponseEntity<?> annualUpdate(@PathVariable Long id, @RequestBody @Valid ManagerRequest.AnnualInDTO annualInDTO, Error error) {
        manageService.연차수정(id, annualInDTO);
        ResponseDTO<?>responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    // role까지 변경가능, master로 접근해야, role변경이 가능하다.
    @PostMapping("/master/{id}")
    public ResponseEntity<?> roleUpdate(@PathVariable Long id,@RequestBody ManagerRequest.MasterInDTO masterInDTO){
        manageService.권한수정(id, masterInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/admin/leave")
    public ResponseEntity<?> getLeave() {
        List<LeaveResponse.InfoOutDTO> waitingLeaves = leaveService.상태선택연차당직정보가져오기(LeaveStatus.WAITING);
        ResponseDTO<List<LeaveResponse.InfoOutDTO>> responseDTO = new ResponseDTO<>(waitingLeaves);
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