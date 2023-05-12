package kr.co.lupintech.controller;

import kr.co.lupintech.dto.leave.LeaveResponse;
import kr.co.lupintech.dto.manager.ManagerRequest;
import kr.co.lupintech.dto.manager.ResponsePagenation;
import kr.co.lupintech.service.LeaveService;
import kr.co.lupintech.service.ManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    // 회원 정보 변경 로직
    @PostMapping("/admin/annual/{id}")
    public ResponseEntity<?> annualUpdate(@PathVariable Long id, @RequestBody @Valid ManagerRequest.AnnualInDTO annualInDTO) {
        manageService.연차수정(id, annualInDTO);
        ResponseDTO<?>responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    // role까지 변경가능, master로 접근해야, role변경이 가능하다.
    @PostMapping("/master/{id}")
    public ResponseEntity<?> roleUpdate(@PathVariable Long id,@RequestBody ManagerRequest.MasterInDTO masterInDTO){

        System.out.println("1");
        manageService.권한수정(id, masterInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/admin") //  /admin?page=5&size=20
    public ResponseEntity<?> userChart(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ManagerRequest.ManageUserListDTO> userListPG = manageService.회원목록보기(pageRequest);

        int pageSize = userListPG.getSize()/size;
        if(userListPG.getSize()%size != 0){
            pageSize++;
        }
        System.out.println(pageSize);

        ResponsePagenation<?,?> responsePagenation = new ResponsePagenation<>(userListPG,pageSize);
        return ResponseEntity.ok().body(responsePagenation);
    }

    @GetMapping("/admin/leave")
    public ResponseEntity<?> getLeave() {
        List<LeaveResponse.InfoOutDTO> waitingLeaves = leaveService.상태선택연차당직정보가져오기(LeaveStatus.WAITING);
        ResponseDTO<List<LeaveResponse.InfoOutDTO>> responseDTO = new ResponseDTO<>(waitingLeaves);
        return ResponseEntity.ok().body(responseDTO);
    }
}