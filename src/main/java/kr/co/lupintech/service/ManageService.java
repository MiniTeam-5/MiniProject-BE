package kr.co.lupintech.service;

import kr.co.lupintech.dto.manager.ManagerRequest;
import kr.co.lupintech.core.annotation.MyErrorLog;
import kr.co.lupintech.dto.PageDTO;
import kr.co.lupintech.dto.manage.ManageResponse;
import kr.co.lupintech.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @MyErrorLog
    @Transactional
    public PageDTO<ManageResponse.UserOutDTO, User> 사원검색(String query, Pageable pageable) {
        Page<User> userPG = query.isBlank() ? // 검색할 query가 없으면 전체 목록 조회(퇴사한 회원은 조회x)
                userRepository.findAll(pageable) : userRepository.findAllByQuery(query, pageable);

        List<ManageResponse.UserOutDTO> content = userPG.getContent().stream()
                .map(user -> new ManageResponse.UserOutDTO(user))
                .collect(Collectors.toList());
        return new PageDTO<>(content, userPG);
    }
}
