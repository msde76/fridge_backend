package capstone.fridge.domain.member.domain.repository;

import capstone.fridge.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
