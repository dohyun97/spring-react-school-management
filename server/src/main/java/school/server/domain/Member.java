package school.server.domain;

import lombok.*;
import school.server.dto.Member.MemberDto;

import javax.persistence.*;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean isFirst;

    @Builder
    public Member(Long id,String name, String email,String username, String password, Role role,Boolean isFirst) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isFirst = isFirst;
    }

    public void updateMember(String username,String email,String password){
        this.email = email;
        this.password = password;
        this.username = username;
        this.isFirst = false;
    }

    public MemberDto toDto(){
       return MemberDto.builder()
                .id(id)
                .name(name)
                .username(username)
                .email(email)
                .password(password)
                .role(role)
                .build();
    }
}
