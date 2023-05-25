package school.server.dto.Member;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDto {
    @NotBlank(message = "Please type username")
    private String username;
    @NotBlank(message = "Please type password")
    private String password;
}
