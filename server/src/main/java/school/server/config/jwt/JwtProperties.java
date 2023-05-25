package school.server.config.jwt;

public interface JwtProperties {
    String secret = "school_Management+System/dohyun_kimSpring*bootWithReact@3059";
    int EXPIRATION_TIME = 6000*10; // 1mins
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
