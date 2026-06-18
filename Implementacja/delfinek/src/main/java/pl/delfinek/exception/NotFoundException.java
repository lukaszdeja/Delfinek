package pl.delfinek.exception;

/**
 * Rzucany, gdy żądany zasób (użytkownik, zajęcia, zapis, tor,...)
 * nie istnieje w repozytorium (odpowiednik HTTP 404).
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
