package kr.co.lupintech.controller;

import kr.co.lupintech.dto.leave.LeaveRequest;
import kr.co.lupintech.dto.leave.LeaveResponse;
import kr.co.lupintech.core.annotation.MyErrorLog;
import kr.co.lupintech.core.annotation.MyLog;
import kr.co.lupintech.dto.PageDTO;
import kr.co.lupintech.dto.user.UserRequest;
import kr.co.lupintech.dto.user.UserResponse;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.service.LeaveService;
import kr.co.lupintech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import kr.co.lupintech.dto.ResponseDTO;
import kr.co.lupintech.model.leave.enums.LeaveStatus;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class AdminController {
    private final LeaveService leaveService;
    private final UserService userService;

    @PostMapping("/admin/annual/{id}")
    public ResponseEntity<?> annualUpdate(@PathVariable Long id, @RequestBody @Valid UserRequest.AnnualInDTO annualInDTO) {
        userService.연차수정(id, annualInDTO);
        ResponseDTO<?>responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    @MyLog
    @MyErrorLog
    @GetMapping("/admin") // /admin?query=김&page=0&size=10
    public ResponseEntity<?> search(@RequestParam(defaultValue = "") String query,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        PageDTO<UserResponse.UserOutDTO, User> pageDTO = userService.사원검색(query, pageable);
        ResponseDTO<PageDTO<UserResponse.UserOutDTO, User>> responseDTO = new ResponseDTO<>(pageDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/admin/resign/{id}")
    public ResponseEntity<?> resign(@PathVariable Long id){
        userService.퇴사(id);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/admin/approve")
    public ResponseEntity<?> decide(@RequestBody @Valid LeaveRequest.DecideInDTO decideInDTO, Errors errors){
        LeaveResponse.DecideOutDTO decideOutDTO = leaveService.연차당직결정하기(decideInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(decideOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/admin/leave")
    public ResponseEntity<?> getLeave() {
        List<LeaveResponse.InfoOutDTO> waitingLeaves = leaveService.상태선택연차당직정보가져오기(LeaveStatus.WAITING);
        ResponseDTO<List<LeaveResponse.InfoOutDTO>> responseDTO = new ResponseDTO<>(waitingLeaves);
        return ResponseEntity.ok().body(responseDTO);
    }
}