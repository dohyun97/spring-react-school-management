package school.server.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.server.domain.Member;
import school.server.dto.Member.MemberDto;
import school.server.dto.Member.MemberUpdateDto;
import school.server.excpetion.customExceptions.NoMemberException;
import school.server.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberServiceImpl implements MemberService{
   private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
   @Transactional
    @Override
    public Long join(MemberDto memberDto) {
       memberDto.setFirst(true);
       memberDto.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
       Member member = memberDto.toEntity();
       isDuplicatedUsername(member);

       Long id = memberRepository.save(member).getId();
        return id;
    }

    private void isDuplicatedUsername(Member member) {
        if(! memberRepository.findByUsername(member.getUsername()).isEmpty()){
            throw new IllegalStateException("This username already exists");
        }
    }

    @Override
    public List<MemberDto> allMembers() {
        List<MemberDto> allMembers= new ArrayList<MemberDto>();
        List<Member> all = memberRepository.findAll();
        for (Member member : all) {
           allMembers.add(member.toDto());
        }
        return allMembers;
    }

    @Override
    public Optional<MemberDto> findMember(Long id) {
        return memberRepository.findById(id).stream()
                .map(member -> member.toDto())
                .findFirst();

    }

    @Override
    public Optional<MemberDto> findMemberUsername(String username) {
        return memberRepository.findByUsername(username).stream()
                .map(member -> member.toDto())
                .findFirst();
    }
    @Transactional
    @Override
    public void updateMember(Long id, MemberUpdateDto updateParam) {
        Member member = memberRepository.findById(id).orElseThrow(()->new NoMemberException("No such a member exists"));
        //log.info("member = {}",member);
        member.updateMember(updateParam.getUsername(), updateParam.getEmail(), updateParam.getPassword());
    }

    @Transactional
    @Override
    public void deleteMember(Long id) {
         memberRepository.deleteById(id);
    }
}
