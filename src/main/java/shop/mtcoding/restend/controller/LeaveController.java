package shop.mtcoding.restend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.leave.LeaveRequest;
import shop.mtcoding.restend.dto.leave.LeaveResponse;
import shop.mtcoding.restend.service.LeaveService;

import javax.validation.Valid;

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
}
