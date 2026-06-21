package pl.delfinek.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.Rola;

class TokenSerwisImplTest {

    private final TokenSerwisImpl service = new TokenSerwisImpl();

    @Test
    void shouldGenerateAndVerifyToken() {
        String token = service.wygenerujToken(1L, Rola.KLIENT);
        assertNotNull(token);
        assertTrue(service.weryfikujToken(token));
        assertEquals(1L, service.pobierzIdUzytkownika(token));
    }

    @Test
    void shouldInvalidateUnknownToken() {
        assertFalse(service.weryfikujToken("unknown"));
        assertThrows(IllegalArgumentException.class,
                () -> service.pobierzIdUzytkownika("unknown"));
    }
}
