package com.be.auth.controller;

import com.be.auth.dto.AuthDtos.*;
import com.be.auth.service.AuthService;
import com.be.user.domain.User;
import com.be.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.be.global.response.CommonResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "인증 / 세션 관련 API")
public class AuthController {

    private static final String SESSION_USER_ID = "USER_ID";

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입", description = "사용자 계정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "요청값 오류"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public CommonResponse<Void> signup(@RequestBody SignupReq req) {
        authService.signup(req);
        return CommonResponse.success(null);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디/비밀번호로 로그인하고 세션을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "요청값 오류"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public CommonResponse<MeRes> login(@RequestBody LoginReq req, HttpServletRequest request) {
        User user = authService.authenticate(req);

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER_ID, user.getId());

        return CommonResponse.success(
                new MeRes(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().name()
                )
        );
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "로그아웃", description = "현재 로그인된 세션을 만료시킵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public CommonResponse<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return CommonResponse.success(null);
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public CommonResponse<MeRes> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Object idObj = session.getAttribute(SESSION_USER_ID);
        if (!(idObj instanceof Long userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return CommonResponse.success(
                new MeRes(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().name()
                )
        );
    }
}
