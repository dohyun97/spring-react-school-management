package school.server.service.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import school.server.domain.Member;
import school.server.domain.Role;
import school.server.dto.Member.MemberDto;
import school.server.dto.Member.MemberUpdateDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class MemberServiceImplTest {
   @Autowired
   MemberService memberService;

    @Test
    void joinTest(){
        MemberDto memberDto = MemberDto.builder()
                .name("dohyun")
                .username("abcdefgh")
                .password("Abcdefgh!")
                .role(Role.STUDENT)
                .email("abcd@gmail.com")
                .build();

        Long id = memberService.join(memberDto);
        memberDto.setId(id);

        MemberDto findMember = memberService.findMember(id).get();
        assertThat(memberDto).isEqualTo(findMember);
    }

    @Test
    void joinExceptionTest(){
        MemberDto memberDto1 = MemberDto.builder()
                .name("dohyun")
                .username("abcdefgh")
                .password("Abcdefgh!")
                .role(Role.STUDENT)
                .email("abcd@gmail.com")
                .build();
        MemberDto memberDto2 = MemberDto.builder()
                .name("dohyun")
                .username("abcdefgh")
                .password("Abcdefgh!")
                .role(Role.STUDENT)
                .email("abcd@gmail.com")
                .build();

        memberService.join(memberDto1);

      assertThrows(IllegalStateException.class,()->memberService.join(memberDto2));
    }

    @Test
    void allMembersTest(){
        MemberDto memberDto = MemberDto.builder()
                .name("dohyun")
                .username("abcdefgh")
                .password("Abcdefgh!")
                .role(Role.STUDENT)
                .email("abcd@gmail.com")
                .build();

        memberService.join(memberDto);
        List<MemberDto> memberDtos = memberService.allMembers();
        assertThat(memberDtos.size()).isEqualTo(1);
    }

    @Test
    void findMemberUsernameTest(){
        MemberDto memberDto = MemberDto.builder()
                .name("dohyun")
                .username("abcdefgh")
                .password("Abcdefgh!")
                .role(Role.STUDENT)
                .email("abcd@gmail.com")
                .build();

        Long id = memberService.join(memberDto);
        memberDto.setId(id);

        MemberDto findMember = memberService.findMemberUsername(memberDto.getUsername()).get();
        assertThat(memberDto).isEqualTo(findMember);
    }

    @Test
    void updateUserTest(){
        MemberDto memberDto = MemberDto.builder()
                .name("dohyun")
                .username("abcdefgh")
                .password("Abcdefgh!")
                .role(Role.STUDENT)
                .email("abcd@gmail.com")
                .build();
        Long id = memberService.join(memberDto);

        MemberUpdateDto updateDto = MemberUpdateDto.builder()
                .email("dohyun@gmail.com")
                .username("abcdefgh")
                .password("Abcdefgh!!!")
                .build();
        memberService.updateMember(id,updateDto);

        MemberDto findMember = memberService.findMember(id).get();
        assertThat(findMember.getUsername()).isEqualTo(updateDto.getUsername());
        assertThat(findMember.getRole()).isEqualTo(memberDto.getRole());
        assertThat(findMember.getPassword()).isEqualTo(updateDto.getPassword());

    }

    @Test
    void deleteMemberTest(){
        MemberDto memberDto = MemberDto.builder()
                .name("dohyun")
                .username("abcdefgh")
                .password("Abcdefgh!")
                .role(Role.STUDENT)
                .email("abcd@gmail.com")
                .build();
        Long id = memberService.join(memberDto);

        memberService.deleteMember(id);
        List<MemberDto> findMembers = memberService.allMembers();
        assertThat(findMembers.size()).isEqualTo(0);
    }
}