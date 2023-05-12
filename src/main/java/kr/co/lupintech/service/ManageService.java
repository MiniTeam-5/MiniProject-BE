package kr.co.lupintech.service;

import kr.co.lupintech.dto.manager.ManagerRequest;
import kr.co.lupintech.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.lupintech.core.annotation.MyLog;
import kr.co.lupintech.core.exception.Exception400;
import kr.co.lupintech.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ManageService {

    private final UserRepository userRepository;

    private Integer pageSize;

    // 회원 관리 페이지, 회원 정보 수정 ( 5/2 김형준 추가)
//    @MyLog
//    @Transactional
//    public ManagerRequest 연차수정(Long id, ManagerRequest.AnnualRequestDTO annualRequestDTO) {
//        ManagerRequest managerRequest = new ManagerRequest();
//        User userPS = userRepository.findByStatusAndId(true,id)
//                .orElseThrow(()->new Exception400("id", "해당 유저가 존재하지 않습니다"));
//        // 정보 수정
//        ManagerRequest.AnnualRequestDTO managePS = new ManagerRequest.AnnualRequestDTO(annualRequestDTO.getRemainDays());
//        userPS.update(managePS.toEntityIn());
//        return managerRequest.toEntityOut(userPS);
//    } // 더티체킹

    // role까지 변경가능
    @MyLog
    @Transactional
    public void 권한수정(Long id, ManagerRequest.MasterInDTO masterInDTO) {
//        ManagerRequest.MasterOutDTO managePS = new ManagerRequest.MasterOutDTO();
        System.out.println("2");
        User userPS = userRepository.findByStatusAndId(true, id)
                .orElseThrow(()->new Exception400("id", "해당 유저가 존재하지 않습니다"));
        // 정보 수정
        System.out.println("3");
        System.out.println(userPS.getRole());
        System.out.println("6");
        userPS.setRole(masterInDTO.getRole());
        System.out.println("4");
        System.out.println(masterInDTO.getRole());
        System.out.println("5");
        System.out.println(userPS.getRole());
//        userPS.update(masterInDTO.toEntityIn(id));
//        return managePS.toEntityOut(userPS);
    } // 더티체킹

    // checkpoint : 유저 목록을 Page객체로 전달할것인가, List객체로 전달할 것인가.
    @MyLog
    @Transactional(readOnly = true)
    public Page<ManagerRequest.ManageUserListDTO> 회원목록보기(Pageable pageable){

        ManagerRequest.ManageUserListDTO manageUserListDTO = new ManagerRequest.ManageUserListDTO();
        List<User> userList = userRepository.findAll();

        Page<ManagerRequest.ManageUserListDTO> usersPG = new PageImpl<>(userList.stream()
                .map(user -> manageUserListDTO.toEntityOut(user))
                .collect(Collectors.toList()), pageable, userList.size());
        return usersPG;
    }
}
