package kr.co.lupintech.service;

import kr.co.lupintech.core.annotation.MyErrorLog;
import kr.co.lupintech.core.annotation.MyLog;
import kr.co.lupintech.core.exception.Exception401;
import kr.co.lupintech.core.factory.AlarmFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.lupintech.core.exception.Exception400;
import kr.co.lupintech.core.exception.Exception500;
import kr.co.lupintech.dto.alarm.AlarmResponse;
import kr.co.lupintech.dto.leave.LeaveRequest;
import kr.co.lupintech.dto.leave.LeaveResponse;
import kr.co.lupintech.model.alarm.Alarm;
import kr.co.lupintech.model.alarm.AlarmRepository;
import kr.co.lupintech.model.leave.Leave;
import kr.co.lupintech.model.leave.LeaveRepository;
import kr.co.lupintech.model.leave.enums.LeaveStatus;
import kr.co.lupintech.model.leave.enums.LeaveType;
import kr.co.lupintech.model.user.User;
import kr.co.lupintech.model.user.UserRepository;
import kr.co.lupintech.model.user.UserRole;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeaveService {
    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;
    private final AlarmRepository alarmRepository;

    private final DateService dateService;
    private final SseService sseService;

    @Transactional
    public LeaveResponse.ApplyOutDTO 연차당직신청하기(LeaveRequest.ApplyInDTO applyInDTO, Long userId) {

        // 1. 유저 존재 확인
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception500("로그인 된 유저가 DB에 존재하지 않음")
        );
        // 2. 탈퇴한 회원인지 확인
        if(userPS.getStatus().equals(false)){
            throw new Exception401("탈퇴한 회원입니다");
        }
        // 3. 당직인 경우
        if(applyInDTO.getType().equals(LeaveType.DUTY)){
            if(!applyInDTO.getStartDate().equals(applyInDTO.getEndDate())){
                throw new Exception400("startDate, endDate", "startDate와 endDate가 같아야 합니다.");
            }

            // 이미 신청한 당직이 있는데 모두 거절 당한 경우는 통과
            List<Leave> duplicateDuty = leaveRepository.findDuplicateDuty(applyInDTO.getType(), applyInDTO.getStartDate(), userId);
            boolean isAllRecjection = true;
            for(Leave duty : duplicateDuty)
            {
                if(LeaveStatus.REJECTION != duty.getStatus())
                {
                    isAllRecjection = false;
                    break;
                }
            }
            if(false == isAllRecjection)//대기, 승인 상태 있으면 중복처리
                throw new Exception400("startDate, endDate", "중복된 당직 신청입니다.");

            // 1) 당직 등록
            Leave leavePS = leaveRepository.save(applyInDTO.toEntity(userPS, 0));

            // 2) 알람 등록
            Alarm alarm = AlarmFactory.newAlarm(userPS, leavePS);
            Alarm alarmPS = alarmRepository.save(alarm);

            // 3) 관리자들에게 실시간 알람 전송 관리자를 향한 알람저장은 안함(알람은 신청자의 정보만 저장한다, 결재자의 정보는 없다).
            //    관리자는 모든 사원의 신청대기 상태의 알람을 불러온다.
            Set<UserRole> adminAndMasterRoles = new HashSet<>(Arrays.asList(UserRole.ROLE_ADMIN, UserRole.ROLE_MASTER));
            List<User> managerList = userRepository.findByRoles(adminAndMasterRoles);
            for (User manager : managerList) {

                try {
                    sseService.sendToUser(manager.getId(), "alarm", new AlarmResponse.AlarmOutDTO(alarmPS));
                    log.info("realtime alarm send to {}: {}, {}, {}", manager.getUsername(), leavePS.getType(), leavePS.getStatus(), leavePS.getStartDate());
                }
                catch (Exception e) {
                    log.error("realtime alram failed to {} : {}, {}, {}", manager.getUsername(), leavePS.getType(), leavePS.getStatus(), leavePS.getStartDate());
                }
            }

            return new LeaveResponse.ApplyOutDTO(leavePS, userPS);
        }
        // 4. 연차인 경우
        // 1) 사용할 연차 일수 계산하기: 평일만 계산 + 공휴일 계산 by 공공 API
        Integer usingDays = -1;
        try{
            usingDays = dateService.getWeekDayCount(applyInDTO.getStartDate(), applyInDTO.getEndDate());
        }catch (URISyntaxException e){
            throw new Exception500(e.getMessage());
        }

        if(usingDays == 0){
            throw new Exception400("startDate, endDate", "연차를 0일 신청했습니다.");
        }
        if(usingDays > userPS.getRemainDays()){
            throw new Exception400("startDate, endDate", "남은 연차보다 더 많이 신청했습니다.");
        }

        // 이미 신청한 연차 있는데 모두 거절 당한 경우는 통과
        List<Leave> duplicateAnnual = leaveRepository.findDuplicateAnnual(applyInDTO.getType(),
                applyInDTO.getStartDate(),
                applyInDTO.getEndDate(),
                userId);
        boolean isAllRecjection = true;
        for(Leave annual : duplicateAnnual)
        {
            if(LeaveStatus.REJECTION != annual.getStatus())
            {
                isAllRecjection = false;
                break;
            }
        }
        if(false == isAllRecjection)//대기, 승인 상태 있으면 중복처리
            throw new Exception400("startDate, endDate", "중복된 연차 신청입니다.");

        // 3) 사용자의 남은 연차 일수 업데이트
        userPS.useAnnualLeave(usingDays);

        // 4) 연차 등록
        Leave leavePS = leaveRepository.save(applyInDTO.toEntity(userPS, usingDays));

        // 5) 알람 등록
        Alarm alarm = AlarmFactory.newAlarm(userPS, leavePS);
        Alarm alarmPS = alarmRepository.save(alarm);

        // 5) 관리자들에게 실시간 알람 전송. 관리자를 향한 알람저장은 안함(알람은 신청자의 정보만 저장한다, 결재자의 정보는 없다).
        // 관리자는 모든 사원의 신청대기 상태의 알람을 불러온다.
        Set<UserRole> adminAndMasterRoles = new HashSet<>(Arrays.asList(UserRole.ROLE_ADMIN, UserRole.ROLE_MASTER));
        List<User> managerList = userRepository.findByRoles(adminAndMasterRoles);
        for (User manager : managerList) {

            try {
                sseService.sendToUser(manager.getId(), "alarm", new AlarmResponse.AlarmOutDTO(alarmPS));
                log.info("realtime alarm send to {}: {}, {}, {}", manager.getUsername(), leavePS.getType(), leavePS.getStatus(), leavePS.getStartDate());
            }
            catch (Exception e) {
                log.error("realtime alram failed to {} : {}, {}, {}", manager.getUsername(), leavePS.getType(), leavePS.getStatus(), leavePS.getStartDate());
            }
        }

        return new LeaveResponse.ApplyOutDTO(leavePS, userPS);
    }

    @Transactional
    public LeaveResponse.CancelOutDTO 연차당직신청취소하기(Long id, Long userId) {
        Leave leavePS = leaveRepository.findById(id).orElseThrow(
                () -> new Exception500("해당 연차/당직 신청 정보가 DB에 존재하지 않음")
        );
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception500("로그인 된 유저가 DB에 존재하지 않음")
        );
        if(userPS.getStatus().equals(false)){
            throw new Exception401("탈퇴한 회원입니다");
        }

        if(leavePS.getStatus().equals(LeaveStatus.APPROVAL)){
            throw new Exception400("id", "이미 승인된 신청입니다.");
        }
        if(leavePS.getStatus().equals(LeaveStatus.REJECTION)){
            throw new Exception400("id", "이미 거절된 신청입니다.");
        }

        if(leavePS.getType().equals(LeaveType.ANNUAL)) {
            userPS.increaseRemainDays(leavePS.getUsingDays());
        }

        leaveRepository.delete(leavePS);

        return new LeaveResponse.CancelOutDTO(userPS);
    }

    @Transactional
    public LeaveResponse.DecideOutDTO 연차당직결정하기(LeaveRequest.DecideInDTO decideInDTO) {
        Leave leavePS = leaveRepository.findById(decideInDTO.getId()).orElseThrow(
                () -> new Exception500("해당 연차/당직 신청 정보가 DB에 존재하지 않음")
        );
        User userPS = leavePS.getUser();
        if (!userPS.getStatus()) {
            throw new Exception500("탈퇴한 회원의 신청입니다.");
        }
        if (leavePS.getStatus().equals(LeaveStatus.APPROVAL)) {
            throw new Exception400("id", "이미 승인된 신청입니다.");
        }
        if (leavePS.getStatus().equals(LeaveStatus.REJECTION)) {
            throw new Exception400("id", "이미 거절된 신청입니다.");
        }

        boolean isReject = false;
        if (decideInDTO.getStatus().equals(LeaveStatus.APPROVAL)) {
            leavePS.setStatus(LeaveStatus.APPROVAL);
        } else {
            leavePS.setStatus(LeaveStatus.REJECTION);
            isReject = true;
        }

        Alarm alarm = null;
        if (leavePS.getType().equals(LeaveType.ANNUAL)) {
            if (isReject) userPS.increaseRemainDays(leavePS.getUsingDays());
            alarm = AlarmFactory.newAlarm(userPS, leavePS);
        } else {
            alarm = AlarmFactory.newAlarm(userPS, leavePS);
        }

        Alarm alarmPS = alarmRepository.save(alarm);
        try {
            sseService.sendToUser(userPS.getId(), "alarm", new AlarmResponse.AlarmOutDTO(alarmPS));
            log.info("realtime alarm send to {}: {}, {}, {}", userPS.getUsername(), leavePS.getType(), leavePS.getStatus(), leavePS.getStartDate());
        }
        catch (Exception e) {
            log.error("realtime alram failed to {} : {}, {}, {}", userPS.getUsername(), leavePS.getType(), leavePS.getStatus(), leavePS.getStartDate());
        }

        return new LeaveResponse.DecideOutDTO(userPS);
    }


    @Transactional(readOnly = true)
    public List<LeaveResponse.InfoOutDTO> 상태선택연차당직정보가져오기(LeaveStatus status)
    {
        // 대기 상태의 모든 연차/당직 정보 가져오기
        List<Leave> leaves = leaveRepository.findByStatus(status);

        // 반환할 DTO 리스트 생성
        List<LeaveResponse.InfoOutDTO> infoOutDTOList = leaves.stream()
                .map(leave -> new LeaveResponse.InfoOutDTO(leave, leave.getUser()))
                .collect(Collectors.toList());

        return infoOutDTOList;
    }

    @Transactional(readOnly = true)
    //모든 유저의 특정 월 정보
    public List<LeaveResponse.InfoOutDTO> 연차당직정보가져오기세달치(String month) {

        // '연도-월' 형식 검증
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        try {
            TemporalAccessor parsedMonth = formatter.parse(month);
        } catch (DateTimeParseException e) {
            throw new Exception400("InvalidFormat", "날짜 형식은 yyyy-MM 이어야 함");
        }

        month += "-01"; // '연도-월' 에 '-일' 붙이기

        LocalDate currentDate = LocalDate.parse(month);
        LocalDate startDate = currentDate.minusMonths(1).withDayOfMonth(1); // 이전 달의 첫날
        LocalDate endDate = currentDate.plusMonths(1).withDayOfMonth(currentDate.getMonth().plus(1).length(currentDate.isLeapYear())); // 다음 달의 마지막 날

        // startDate와 endDate 사이에 있는 모든 연차/당직 정보 가져오기
        List<Leave> leaves = leaveRepository.findAll()
                .stream()
                .filter(leave -> (leave.getStartDate().compareTo(startDate) >= 0 && leave.getStartDate().compareTo(endDate) <= 0)
                        || (leave.getEndDate().compareTo(startDate) >= 0 && leave.getEndDate().compareTo(endDate) <= 0)
                        || (leave.getStartDate().compareTo(startDate) <= 0 && leave.getEndDate().compareTo(endDate) >= 0))
                .collect(Collectors.toList());

        // 반환할 DTO 리스트 생성
        List<LeaveResponse.InfoOutDTO> infoOutDTOList = leaves.stream()
                .map(leave -> new LeaveResponse.InfoOutDTO(leave, leave.getUser()))
                .collect(Collectors.toList());

        return infoOutDTOList;
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse.InfoOutDTO> 모두의모든연차당직가져오기() {
        List<Leave> leaves = leaveRepository.findAll();

        List<LeaveResponse.InfoOutDTO> infoOutDTOList = leaves.stream()
                .map(leave -> new LeaveResponse.InfoOutDTO(leave, leave.getUser()))
                .collect(Collectors.toList());

        return infoOutDTOList;
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse.InfoOutDTO> 특정유저연차당직정보가져오기(Long userId) {

        // 특정 유저의 모든 연차/당직 정보 가져오기
        List<Leave> leaves = leaveRepository.findAllByUserId(userId);

        // 반환할 DTO 리스트 생성
        List<LeaveResponse.InfoOutDTO> infoOutDTOList = leaves.stream()
                .map(leave -> new LeaveResponse.InfoOutDTO(leave, leave.getUser()))
                .collect(Collectors.toList());

        return infoOutDTOList;
    }

    @MyLog
    @MyErrorLog
    @Transactional
    public Resource 엑셀다운로드() throws IOException {
        List<Leave> leaves = leaveRepository.findAll(); // 사용자가 100명 이상이 되면 join fetch 필요.

        // 액셀 파일 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Leaves");

        // 헤더 쓰기
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("User ID");
        headerRow.createCell(2).setCellValue("User Name");
        headerRow.createCell(3).setCellValue("Type");
        headerRow.createCell(4).setCellValue("Start Date");
        headerRow.createCell(5).setCellValue("End Date");
        headerRow.createCell(6).setCellValue("Using Days");
        headerRow.createCell(7).setCellValue("Status");

        int rowNum = 1;
        for (Leave leave : leaves) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(leave.getId());
            row.createCell(1).setCellValue(leave.getUser().getId());
            row.createCell(2).setCellValue(leave.getUser().getUsername());
            row.createCell(3).setCellValue(leave.getType().toString());
            row.createCell(4).setCellValue(leave.getStartDate().toString());
            row.createCell(5).setCellValue(leave.getEndDate().toString());
            row.createCell(6).setCellValue(leave.getUsingDays());
            row.createCell(7).setCellValue(leave.getStatus().toString());
        }

        // 액셀 파일을 임시 디렉토리에 저장
        File tempFile = File.createTempFile("temp", ".xlsx"); //임시 디렉토리에 확장자가 ".xlsx"인 임시 파일을 생성 - 임시 파일은 애플리케이션 실행 중에만 유효하고 임시 파일이 더 이상 필요하지 않을 때 삭제
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            workbook.write(fos); // workbook 객체를 사용하여 액셀 파일 데이터를 FileOutputStream으로 출력하여 tempFile에 작성
        }

        return new FileSystemResource(tempFile); // 생성된 FileSystemResource 객체는 액셀 파일을 나타내는 리소스
    }
}
