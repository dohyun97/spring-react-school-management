package school.server.dto;

import lombok.Data;

@Data
public class ErrorResult {
    private boolean success = false;
    private Integer code;
    private String message;

    public ErrorResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
