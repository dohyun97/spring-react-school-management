package school.server.excpetion.customExceptions;

public class NoMemberException extends RuntimeException{
    public NoMemberException() {
        super();
    }

    public NoMemberException(String message) {
        super(message);
    }
}
