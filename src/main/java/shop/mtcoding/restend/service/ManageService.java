package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.annotation.MyLog;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.dto.manage.Manage;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ManageService {

    private UserRepository userRepository;

    private Integer pageSize;

    // 회원 관리 페이지, 회원 정보 수정 ( 5/2 김형준 추가)
    @MyLog
    @Transactional
    public Manage 연차수정(Long id, Manage.AnnualRequestDTO annualRequestDTO) {
        Manage manage = new Manage();
        User userPS = userRepository.findById(id)
                .orElseThrow(()->new Exception400("id", "해당 유저가 존재하지 않습니다"));
        // 정보 수정
        Manage.AnnualRequestDTO managePS = new Manage.AnnualRequestDTO(annualRequestDTO.getRemain_days());
        userPS.update(managePS.toEntityIn());
        return manage.toEntityOut(userPS);
    } // 더티체킹

    // role까지 변경가능
    @MyLog
    @Transactional
    public Manage.MasterOutDTO 권한수정(Long id, Manage.MasterInDTO masterIn) {
        Manage.MasterOutDTO managePS = new Manage.MasterOutDTO();
        User userPS = userRepository.findById(id)
                .orElseThrow(()->new Exception400("id", "해당 유저가 존재하지 않습니다"));
        // 정보 수정
        userPS.update(masterIn.toEntityIn(id));
        return managePS.toEntityOut(userPS);
    } // 더티체킹

    // checkpoint : 유저 목록을 Page객체로 전달할것인가, List객체로 전달할 것인가.
    @MyLog
    @Transactional(readOnly = true)
    public Page<Manage.UserManageDTO> 회원목록보기(Pageable pageable){

        Manage.UserManageDTO userManageDTO = new Manage.UserManageDTO();
        List<User> userList = userRepository.findAll();

        Page<Manage.UserManageDTO> usersPG = new PageImpl<>(userList.stream().map(user -> userManageDTO.toEntityOut(user) ).collect(Collectors.toList()), pageable, userList.size());
        return usersPG;
    }
}
