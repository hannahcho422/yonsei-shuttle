package com.yonsei.shuttle.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsei.shuttle.auth.dto.LoginRequest;
import com.yonsei.shuttle.auth.dto.SignupRequest;
import com.yonsei.shuttle.auth.dto.TokenResponse;
import com.yonsei.shuttle.auth.dto.UserResponse;
import com.yonsei.shuttle.common.exception.CustomException;
import com.yonsei.shuttle.common.exception.ErrorCode;
import com.yonsei.shuttle.common.util.JwtUtil;
import com.yonsei.shuttle.user.domain.User;
import com.yonsei.shuttle.user.domain.UserRole;
import com.yonsei.shuttle.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증/인가 비즈니스 로직 (회원가입, 로그인, 토큰 재발급)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @Transactional
    public UserResponse signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 생성 (기본 권한: USER)
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .role(UserRole.USER)
                .build();

        User saved = userRepository.save(user);
        log.info("회원가입 완료: userId={}, email={}", saved.getUserId(), saved.getEmail());

        return UserResponse.from(saved);
    }

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // JWT 발급
        String accessToken = jwtUtil.createAccessToken(
                user.getUserId(), user.getEmail(), user.getRole().name()
        );
        String refreshToken = jwtUtil.createRefreshToken(
                user.getUserId(), user.getEmail(), user.getRole().name()
        );

        log.info("로그인 성공: userId={}, email={}", user.getUserId(), user.getEmail());
        return TokenResponse.of(accessToken, refreshToken);
    }

    /**
     * Access Token 재발급 (Refresh Token 사용)
     */
    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        // Refresh Token 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Integer userId = jwtUtil.getUserId(refreshToken);
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // DB에서 사용자 존재 여부 재확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새 토큰 발급
        String newAccessToken = jwtUtil.createAccessToken(
                user.getUserId(), user.getEmail(), user.getRole().name()
        );
        String newRefreshToken = jwtUtil.createRefreshToken(
                user.getUserId(), user.getEmail(), user.getRole().name()
        );

        log.info("토큰 재발급: userId={}, email={}", user.getUserId(), email);
        return TokenResponse.of(newAccessToken, newRefreshToken);
    }
}