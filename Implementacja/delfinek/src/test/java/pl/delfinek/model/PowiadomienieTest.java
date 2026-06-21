package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.TypPowiadomienia;

class PowiadomienieTest {

    private Uzytkownik odbiorca;
    private Powiadomienie powiadomienie;

    @BeforeEach
    void setUp() {
        odbiorca = new Klient(1L, "Jan", "Kowalski", "jan@example.com", "pass", "123", null);
        powiadomienie = new Powiadomienie(1L, "Tytul", "Tresc", TypPowiadomienia.KOMUNIKAT_OGOLNY, odbiorca);
    }

    @Test
    void shouldCreateNotification() {
        assertNotNull(powiadomienie.getDataCzas());
        assertFalse(powiadomienie.isPrzeczytane());
        assertEquals("Tytul", powiadomienie.getTytul());
        assertEquals("Tresc", powiadomienie.getTresc());
        assertEquals(odbiorca, powiadomienie.getOdbiorca());
    }

    @Test
    void shouldMarkAsRead() {
        powiadomienie.oznaczJakoPrzeczytane();
        assertTrue(powiadomienie.isPrzeczytane());
    }
}
