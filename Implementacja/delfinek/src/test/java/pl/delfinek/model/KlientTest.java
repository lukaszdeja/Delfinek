package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.PoziomPlywania;
import pl.delfinek.model.enums.TypZajec;

class KlientTest {

    private Klient klient;
    private Zajecia zajecia;

    @BeforeEach
    void setUp() {
        klient = new Klient(1L, "Jan", "Kowalski", "jan@example.com", "pass", "123", null);
        zajecia = new Zajecia(1L, LocalDateTime.now().plusDays(1), 45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
    }

    @Test
    void shouldHaveDefaultLevel() {
        assertEquals(PoziomPlywania.POCZATKUJACY, klient.getPoziomPlywania());
    }

    @Test
    void shouldSetLevel() {
        klient.setPoziomPlywania(PoziomPlywania.SREDNI);
        assertEquals(PoziomPlywania.SREDNI, klient.getPoziomPlywania());
    }

    @Test
    void shouldSetNote() {
        klient.setNotatkaInstruktora("Postępy dobre");
        assertEquals("Postępy dobre", klient.getNotatkaInstruktora());
    }

    @Test
    void shouldEnrollToClassViaMethod() {
        klient.zapiszSieNaZajecia(zajecia);
        assertTrue(zajecia.getListaUczestnikow().contains(klient));
    }

    @Test
    void shouldThrowWhenNullZajecia() {
        assertThrows(IllegalArgumentException.class, () -> klient.zapiszSieNaZajecia(null));
    }

    @Test
    void shouldCancelEnrollment() {
        Zapis zapis = new Zapis(1L, klient, zajecia);
        klient.anulujZapis(zapis);
        assertEquals(pl.delfinek.model.enums.StatusZapisu.ANULOWANY, zapis.getStatus());
        assertFalse(zajecia.getListaUczestnikow().contains(klient));
    }

    @Test
    void shouldThrowWhenNullZapis() {
        assertThrows(IllegalArgumentException.class, () -> klient.anulujZapis(null));
    }
}
