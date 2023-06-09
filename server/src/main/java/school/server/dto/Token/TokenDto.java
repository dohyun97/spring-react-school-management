package school.server.dto.Token;

import lombok.Builder;
import lombok.Data;

@Data
public class TokenDto {
    private boolean success = true;
    private String accessToken;
    private String refreshToken;


    public TokenDto(String accessToken, String refreshToken) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

    }
}
