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


    @MyLog
    @Transactional
    public void 연차수정(Long id, ManagerRequest.AnnualInDTO annualInDTO) {
        User userPS = userRepository.findByStatusAndId(true,id)
                .orElseThrow(()->new Exception400("id", "해당 유저가 존재하지 않습니다"));
        // 정보 수정
        userPS.setRemainDays(annualInDTO.getRemainDays());
    }

    // role까지 변경가능
    @MyLog
    @Transactional
    public void 권한수정(Long id, ManagerRequest.MasterInDTO masterInDTO) {
        User userPS = userRepository.findByStatusAndId(true, id)
                .orElseThrow(()->new Exception400("id", "해당 유저가 존재하지 않습니다"));
        // 정보 수정
        userPS.setRole(masterInDTO.getRole());
    }

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
