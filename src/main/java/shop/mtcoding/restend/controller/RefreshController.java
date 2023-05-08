package shop.mtcoding.restend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.service.RefreshService;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class RefreshController
{
    private final RefreshService refreshService;

    @PostMapping("/refreshtoken/{id}")//id == userId
    public ResponseEntity<?> refreshToken(@PathVariable Long id, HttpServletRequest request) {

        // RefreshService를 통해 액세스 토큰 재발급
        String accessjwt = refreshService.액세스재발급(id, request);

        return ResponseEntity.ok().header(MyJwtProvider.HEADER, accessjwt).build();
    }
}
