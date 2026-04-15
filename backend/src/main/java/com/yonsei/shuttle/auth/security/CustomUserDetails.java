package com.yonsei.shuttle.auth.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.yonsei.shuttle.user.domain.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Security 인증 객체 (User 엔티티 래핑)
 * 역할: @AuthenticationPrincipal 로 Controller에서 꺼내 쓸 수 있음
 * 실행: SecurityContext에 담기는 객체
 *
 * SecurityContextHolder에 저장되어 모든 Controller/Service에서 접근 가능:
 *   SecurityContextHolder.getContext().getAuthentication().getPrincipal()
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    public Integer getUserId() {
        return user.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_USER" 또는 "ROLE_ADMIN" 으로 접두어 붙여 반환
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Spring Security 내부에서 식별자로 email 사용
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}