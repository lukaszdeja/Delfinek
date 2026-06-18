package pl.delfinek.exception;

/**
 * Rzucany przy konflikcie terminu - tor lub instruktor są już zajęci w danym
 * przedziale czasowym. Odpowiada HTTP 409.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
