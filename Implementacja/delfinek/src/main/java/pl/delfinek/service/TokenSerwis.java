package pl.delfinek.service;

import pl.delfinek.model.enums.Rola;

/**
 * Serwis odpowiedzialny za generowanie i weryfikację tokenów uwierzytelniających
 * (np. JWT w realnym systemie). Tutaj uproszczona implementacja in-memory.
 */
public interface TokenSerwis {

    String wygenerujToken(Long userId, Rola rola);

    boolean weryfikujToken(String token);

    Long pobierzIdUzytkownika(String token);
}
