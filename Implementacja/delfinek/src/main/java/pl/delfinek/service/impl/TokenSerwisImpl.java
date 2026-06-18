package pl.delfinek.service.impl;

import pl.delfinek.model.enums.Rola;
import pl.delfinek.service.TokenSerwis;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Uproszczona implementacja TokenSerwis - generuje losowy token (UUID) i przechowuje
 * mapowanie token -> userId w pamięci. Brak realnego JWT,
 * ponieważ projekt nie zakłada integracji z prawdziwym API.
 */
public class TokenSerwisImpl implements TokenSerwis {

    private final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();
    private final Map<String, Rola> tokenToRola = new ConcurrentHashMap<>();

    @Override
    public String wygenerujToken(Long userId, Rola rola) {
        if (userId == null) {
            throw new IllegalArgumentException("userId nie może być null.");
        }
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, userId);
        tokenToRola.put(token, rola);
        return token;
    }

    @Override
    public boolean weryfikujToken(String token) {
        return token != null && tokenToUserId.containsKey(token);
    }

    @Override
    public Long pobierzIdUzytkownika(String token) {
        Long id = tokenToUserId.get(token);
        if (id == null) {
            throw new IllegalArgumentException("Nieprawidłowy lub wygasły token.");
        }
        return id;
    }
}
