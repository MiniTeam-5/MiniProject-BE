package shop.mtcoding.restend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.leave.LeaveRequest;
import shop.mtcoding.restend.dto.leave.LeaveResponse;
import shop.mtcoding.restend.service.LeaveService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class LeaveController {

    private final LeaveService leaveService;
    @PostMapping("/auth/leave/apply")
    public ResponseEntity<?> apply(@RequestBody @Valid LeaveRequest.ApplyInDTO applyInDTO, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails){
        LeaveResponse.ApplyOutDTO applyOutDTO = leaveService.연차당직신청하기(applyInDTO, myUserDetails.getUser().getId());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(applyOutDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/auth/leave")
    public ResponseEntity<?> getLeaveData(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String week,
            @RequestParam(required = false) String day) {
        List<LeaveResponse.InfoOutDTO> leaveDataList = leaveService.getLeaveData(id, month, week, day);
        ResponseDTO<List<LeaveResponse.InfoOutDTO>> responseDTO = new ResponseDTO<>(leaveDataList);
        return ResponseEntity.ok(responseDTO);
    }
}
