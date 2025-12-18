//package capstone.fridge.global.config.security;
//
//import capstone.fridge.domain.member.domain.entity.Member;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.Collections;
//
//@Getter
//@RequiredArgsConstructor
//public class PrincipalDetails implements UserDetails {
//
//    private final Member member; // 콤포지션: Member 객체를 품고 있음
//
//    // 권한 반환 (일반 유저라면 "ROLE_USER" 등)
//    // 현재 Member 엔티티에 Role 필드가 없다면 빈 리스트를 반환하거나 임의로 설정
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        // 예시: 무조건 USER 권한 부여
//        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
//    }
//
//    @Override
//    public String getPassword() {
//        return null; // 카카오 로그인은 비밀번호가 없으므로 null 반환 (또는 더미 값)
//    }
//
//    @Override
//    public String getUsername() {
//        // Spring Security가 식별할 수 있는 유니크한 값 (보통 ID나 Email)
//        return member.getId().toString();
//    }
//
//    // 계정 만료 여부 (true: 만료 안됨)
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    // 계정 잠김 여부 (true: 잠기지 않음)
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    // 비밀번호 만료 여부 (true: 만료 안됨)
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    // 계정 활성화 여부 (true: 활성화)
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}