package school.server.excpetion;

public class NoMemberException extends RuntimeException{
    public NoMemberException() {
        super();
    }

    public NoMemberException(String message) {
        super(message);
    }
}
