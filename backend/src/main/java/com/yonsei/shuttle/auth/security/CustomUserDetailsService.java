package com.yonsei.shuttle.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsei.shuttle.common.exception.CustomException;
import com.yonsei.shuttle.common.exception.ErrorCode;
import com.yonsei.shuttle.user.domain.User;
import com.yonsei.shuttle.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 로그인 시 Spring Security가 호출하는 서비스
 * 이메일로 User 조회 후 CustomUserDetails로 래핑
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        return new CustomUserDetails(user);
    }
}