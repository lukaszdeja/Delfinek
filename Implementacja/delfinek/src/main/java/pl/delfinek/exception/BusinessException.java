package pl.delfinek.exception;

/**
 * Ogólny wyjątek reguł biznesowych - np. "Brak wolnych miejsc" przy zapisie na zajęcia.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
