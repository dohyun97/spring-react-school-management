package school.server.dto.Member;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import school.server.domain.Member;
import school.server.domain.Role;

import javax.validation.constraints.*;

@Data
public class MemberDto {
    private Long id;
    @Email(message = "This is not valid type")
    @NotBlank(message = "Please type email")
    private String email;

    @NotBlank(message = "Please type name")
    private String name;

    @NotBlank(message = "Please type username")
    @Range(max = 20,min = 7,message ="username should be longer than 7 and shorter than 20 characters")
    private String username;

    @NotBlank(message = "Please type password")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",message = "Password should contain at least one upper and lower case letter,number, symbols(~`!@#$%^&*()_-+={[}]|;<,>.?/) and length should be 8~20")
    private String password;

    private boolean isFirst;

    @NotBlank(message = "Please select role")
    private Role role;
    @Builder
    public MemberDto(Long id, String email,String name,String username, String password, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Member toEntity(){
        return Member.builder()
                .id(id)
                .name(name)
                .email(email)
                .username(username)
                .password(password)
                .role(role)
                .isFirst(isFirst)
                .build();
    }
}
