package at.campus.auth.exception;


public class NicknameAlreadyExistsException extends RuntimeException {
    public NicknameAlreadyExistsException(String nickname) {
        super("Nickname ist bereits vergeben.");
    }
}
