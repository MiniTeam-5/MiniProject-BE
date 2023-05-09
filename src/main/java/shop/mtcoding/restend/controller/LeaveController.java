package shop.mtcoding.restend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/auth/leave/{id}/delete")
    public ResponseEntity<?> cancel(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){
        LeaveResponse.CancelOutDTO cancelOutDTO = leaveService.연차당직신청취소하기(id, myUserDetails.getUser().getId());
        ResponseDTO<?> responseDTO = new ResponseDTO<>(cancelOutDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/auth/leave/month/{month}")
    public ResponseEntity<?> getByMonth(@PathVariable(required = true) String month) {
        List<LeaveResponse.InfoOutDTO> leaveDataList = leaveService.연차당직정보가져오기세달치(month);
        ResponseDTO<List<LeaveResponse.InfoOutDTO>> responseDTO = new ResponseDTO<>(leaveDataList);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/auth/leave/id/{id}")
    public ResponseEntity<?> getById(@PathVariable(required = true) Long id) {
        List<LeaveResponse.InfoOutDTO> leaveDataList = leaveService.특정유저연차당직정보가져오기(id);
        ResponseDTO<List<LeaveResponse.InfoOutDTO>> responseDTO = new ResponseDTO<>(leaveDataList);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/admin/approve")
    public ResponseEntity<?> decide(@RequestBody @Valid LeaveRequest.DecideInDTO decideInDTO, Errors errors){
        LeaveResponse.DecideOutDTO decideOutDTO = leaveService.연차당직결정하기(decideInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(decideOutDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
