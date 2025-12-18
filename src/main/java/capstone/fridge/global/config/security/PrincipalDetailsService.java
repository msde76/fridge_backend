//package capstone.fridge.global.config.security;
//
//import capstone.fridge.domain.member.domain.entity.Member;
//import capstone.fridge.domain.member.domain.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class PrincipalDetailsService implements UserDetailsService {
//
//    private final MemberRepository memberRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // username(여기선 memberId)으로 회원 조회
//        Long memberId = Long.parseLong(username);
//
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//
//        return new PrincipalDetails(member);
//    }
//}