package kr.co.lupintech.controller;

import kr.co.lupintech.core.annotation.MyErrorLog;
import kr.co.lupintech.core.annotation.MyLog;
import kr.co.lupintech.core.auth.session.MyUserDetails;
import kr.co.lupintech.core.exception.Exception400;
import kr.co.lupintech.dto.leave.LeaveRequest;
import kr.co.lupintech.dto.leave.LeaveResponse;
import kr.co.lupintech.service.LeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import kr.co.lupintech.dto.ResponseDTO;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
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

    @GetMapping("/auth/leave/all")
    public ResponseEntity<?> getByMonth() {
        List<LeaveResponse.InfoOutDTO> leaveDataList = leaveService.모두의모든연차당직가져오기();
        ResponseDTO<List<LeaveResponse.InfoOutDTO>> responseDTO = new ResponseDTO<>(leaveDataList);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/auth/leave/id/{id}")
    public ResponseEntity<?> getById(@PathVariable(required = true) Long id) {
        List<LeaveResponse.InfoOutDTO> leaveDataList = leaveService.특정유저연차당직정보가져오기(id);
        ResponseDTO<List<LeaveResponse.InfoOutDTO>> responseDTO = new ResponseDTO<>(leaveDataList);
        return ResponseEntity.ok(responseDTO);
    }

    @MyLog
    @MyErrorLog
    @GetMapping("/auth/leave/download")
    public ResponseEntity<?> download() throws IOException {
        Resource resource = leaveService.엑셀다운로드();
        String filename = "annual_leave_duty.xlsx"; // 다운로드할 파일 이름
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"") // 다운로드, 이름지정
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // 일반적인 이진 데이터
                .body(resource);
    }
}
