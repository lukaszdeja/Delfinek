package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KonwersacjaTest {

    private Uzytkownik u1;
    private Uzytkownik u2;
    private Konwersacja konwersacja;

    @BeforeEach
    void setUp() {
        u1 = new Klient(1L, "Jan", "Kowalski", "jan@example.com", "pass", "123", null);
        u2 = new Instruktor(2L, "Piotr", "Nowak", "piotr@example.com", "pass", "456", null, "opis");
        konwersacja = new Konwersacja(1L, u1, u2);
    }

    @Test
    void shouldCreateConversation() {
        assertNotNull(konwersacja.getDataUtworzenia());
        assertEquals(u1, konwersacja.getUczestnik1());
        assertEquals(u2, konwersacja.getUczestnik2());
        assertTrue(konwersacja.getWiadomosci().isEmpty());
    }

    @Test
    void shouldSendMessage() {
        Wiadomosc w = konwersacja.wyslijWiadomosc(u1, "Hello", 1L);
        assertEquals(1, konwersacja.getWiadomosci().size());
        assertEquals("Hello", w.getTresc());
        assertEquals(u1, w.getNadawca());
    }

    @Test
    void shouldThrowWhenEmptyMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> konwersacja.wyslijWiadomosc(u1, " ", 2L));
        assertThrows(IllegalArgumentException.class,
                () -> konwersacja.wyslijWiadomosc(u1, null, 2L));
    }

    @Test
    void shouldCheckParticipants() {
        assertTrue(konwersacja.dotyczyUczestnikow(1L, 2L));
        assertTrue(konwersacja.dotyczyUczestnikow(2L, 1L));
        assertFalse(konwersacja.dotyczyUczestnikow(1L, 3L));
        assertFalse(konwersacja.dotyczyUczestnikow(3L, 2L));
    }
}
