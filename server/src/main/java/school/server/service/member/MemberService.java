package school.server.service.member;


import school.server.dto.Member.MemberDto;
import school.server.dto.Member.MemberUpdateDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Long join(MemberDto memberDto);
    List<MemberDto> allMembers();
    Optional<MemberDto> findMember(Long id);
    Optional<MemberDto> findMemberUsername(String username);
    void updateMember(Long id, MemberUpdateDto updateParam);

    void deleteMember(Long id);
}
