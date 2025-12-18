package capstone.fridge.domain.member.domain.repository;

import capstone.fridge.domain.member.domain.entity.MemberPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPreferenceRepository extends JpaRepository<MemberPreference, Long> {
}
