package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.TypZajec;

class ZajeciaTest {

    private Zajecia zajecia;
    private Klient klient1;
    private Klient klient2;

    @BeforeEach
    void setUp() {
        zajecia = new Zajecia(1L, LocalDateTime.of(2026, Month.JULY, 10, 10, 0),
                60, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        klient1 = new Klient(1L, "Jan", "Kowalski", "jan@example.com", "pass", "123456789", null);
        klient2 = new Klient(2L, "Anna", "Nowak", "anna@example.com", "pass", "987654321", null);
    }

    @Test
    void shouldReturnEndTimeCorrectly() {
        assertEquals(LocalDateTime.of(2026, Month.JULY, 10, 11, 0), zajecia.getDataZakonczenia());
    }

    @Test
    void shouldDetectTimeCollision() {
        LocalDateTime start = LocalDateTime.of(2026, Month.JULY, 10, 10, 30);
        LocalDateTime end = LocalDateTime.of(2026, Month.JULY, 10, 11, 30);
        assertTrue(zajecia.kolidujeCzasowo(start, end));

        start = LocalDateTime.of(2026, Month.JULY, 10, 9, 0);
        end = LocalDateTime.of(2026, Month.JULY, 10, 10, 30);
        assertTrue(zajecia.kolidujeCzasowo(start, end));

        start = LocalDateTime.of(2026, Month.JULY, 10, 9, 0);
        end = LocalDateTime.of(2026, Month.JULY, 10, 10, 0);
        assertFalse(zajecia.kolidujeCzasowo(start, end)); // kończy się dokładnie na starcie
    }

    @Test
    void shouldAddStudent() {
        zajecia.dodajKursanta(klient1);
        assertEquals(1, zajecia.getListaUczestnikow().size());
        assertEquals(9, zajecia.getLiczbaMiejscWolnych());
        assertFalse(zajecia.isPelne());
    }

    @Test
    void shouldThrowWhenNoPlace() {
        Zajecia male = new Zajecia(2L, LocalDateTime.now(), 45, TypZajec.DOROSLI_PODSTAWOWY, 1, Cyklicznosc.JEDNORAZOWE);
        male.dodajKursanta(klient1);
        assertThrows(IllegalStateException.class, () -> male.dodajKursanta(klient2));
    }

    @Test
    void shouldThrowWhenDuplicateStudent() {
        zajecia.dodajKursanta(klient1);
        assertThrows(IllegalStateException.class, () -> zajecia.dodajKursanta(klient1));
    }

    @Test
    void shouldRemoveStudent() {
        zajecia.dodajKursanta(klient1);
        zajecia.usunKursanta(klient1);
        assertEquals(0, zajecia.getListaUczestnikow().size());
        assertEquals(10, zajecia.getLiczbaMiejscWolnych());
    }

    @Test
    void shouldAddStudentIfNotPresent() {
        zajecia.dodajKursantaJesliNieobecny(klient1);
        assertEquals(1, zajecia.getListaUczestnikow().size());
        // drugi raz nie doda
        zajecia.dodajKursantaJesliNieobecny(klient1);
        assertEquals(1, zajecia.getListaUczestnikow().size());
        // null nie robi nic
        zajecia.dodajKursantaJesliNieobecny(null);
        assertEquals(1, zajecia.getListaUczestnikow().size());
    }
}
