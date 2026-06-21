package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.StatusZapisu;
import pl.delfinek.model.enums.TypZajec;

class ZapisTest {

    private Klient klient;
    private Zajecia zajecia;
    private Zapis zapis;

    @BeforeEach
    void setUp() {
        klient = new Klient(1L, "Jan", "Kowalski", "jan@example.com", "pass", "123", null);
        zajecia = new Zajecia(1L, LocalDateTime.now().plusDays(1), 45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        zapis = new Zapis(1L, klient, zajecia);
    }

    @Test
    void shouldCreateActiveEnrollment() {
        assertNotNull(zapis.getDataZapisu());
        assertEquals(StatusZapisu.AKTYWNY, zapis.getStatus());
        assertEquals(klient, zapis.getKlient());
        assertEquals(zajecia, zapis.getZajecia());
    }

    @Test
    void shouldCancelEnrollment() {
        zapis.anuluj();
        assertEquals(StatusZapisu.ANULOWANY, zapis.getStatus());
        // sprawdzenie, czy klient został usunięty z zajęć
        assertFalse(zajecia.getListaUczestnikow().contains(klient));
    }

    @Test
    void shouldThrowWhenAlreadyCancelled() {
        zapis.anuluj();
        assertThrows(IllegalStateException.class, () -> zapis.anuluj());
    }
}
