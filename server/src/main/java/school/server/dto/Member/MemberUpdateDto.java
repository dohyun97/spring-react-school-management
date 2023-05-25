package school.server.dto.Member;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class MemberUpdateDto {
    @NotBlank(message = "Please type email")
    @Email(message = "This is not valid type")
    private String email;

    @NotBlank(message = "Please type username")
    @Range(max = 20,min = 7,message ="username should be longer than 7 and shorter than 20 characters")
    private String username;

    @NotBlank(message = "Please type password")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",message = "Password should contain at least one upper and lower case letter,number, symbols(~`!@#$%^&*()_-+={[}]|;<,>.?/) and length should be 8~20")
    private String password;
   @Builder
    public MemberUpdateDto(String email ,String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
