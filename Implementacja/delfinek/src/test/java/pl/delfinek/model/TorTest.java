package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.StatusToru;
import pl.delfinek.model.enums.TypZajec;
import pl.delfinek.model.enums.Cyklicznosc;

class TorTest {

    private Tor tor;

    @BeforeEach
    void setUp() {
        tor = new Tor(1L, 1, 25, 1.8);
    }

    @Test
    void shouldBeFreeWhenNoCollision() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 6, 10, 0);
        LocalDateTime end = start.plusHours(1);

        // Zajęcia które nie kolidują (godzinę później)
        Zajecia zajecia = new Zajecia(1L, start.plusHours(2), 45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        
        boolean free = tor.isWolny(start, end, List.of(zajecia));
        assertTrue(free, "Tor powinien być wolny, gdy nie ma kolizji");
    }

    @Test
    void shouldNotBeFreeWhenCollisionExists() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 6, 10, 0);
        LocalDateTime end = start.plusHours(1);

        Zajecia zajecia = new Zajecia(1L, start, 45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        
        boolean free = tor.isWolny(start, end, List.of(zajecia));
        assertFalse(free, "Tor nie powinien być wolny, gdy są kolidujące zajęcia");
    }

    @Test
    void shouldNotBeFreeWhenInRepair() {
        tor.zglosRemont(LocalDate.now(), LocalDate.now().plusDays(2));
        
        boolean free = tor.isWolny(
            LocalDateTime.now(), 
            LocalDateTime.now().plusHours(1), 
            List.of()
        );
        assertFalse(free, "Tor nie powinien być wolny podczas remontu");
    }

    @Test
    void shouldBeFreeWhenNoLessons() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        
        boolean free = tor.isWolny(start, end, null);
        assertTrue(free, "Tor powinien być wolny, gdy lista zajęć jest null");
    }

    @Test
    void shouldStartRepair() {
        LocalDate start = LocalDate.of(2026, 8, 1);
        LocalDate end = LocalDate.of(2026, 8, 10);
        
        tor.zglosRemont(start, end);
        
        assertEquals(StatusToru.REMONT, tor.getStatus());
        assertEquals("Remont: 2026-08-01 - 2026-08-10", tor.getUwagi());
    }

    @Test
    void shouldFinishRepair() {
        tor.zglosRemont(LocalDate.now(), LocalDate.now().plusDays(2));
        tor.zakonczRemont();
        
        assertEquals(StatusToru.WOLNY, tor.getStatus());
        assertNull(tor.getUwagi());
    }

    @Test
    void shouldThrowExceptionWhenRepairDatesInvalid() {
        LocalDate start = LocalDate.of(2026, 8, 10);
        LocalDate end = LocalDate.of(2026, 8, 1);
        
        assertThrows(IllegalArgumentException.class, 
            () -> tor.zglosRemont(start, end));
    }

    @Test
    void shouldReturnFalseWhenLessonPartiallyOverlaps() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 6, 10, 0);
        LocalDateTime end = start.plusHours(1);

        // Zajęcia zaczynające się wcześniej i kończące w środku przedziału
        Zajecia zajecia = new Zajecia(1L, start.minusMinutes(30), 60, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        
        boolean free = tor.isWolny(start, end, List.of(zajecia));
        assertFalse(free, "Tor nie powinien być wolny, gdy zajęcia częściowo nakładają się na koniec");
    }

    @Test
    void shouldReturnFalseWhenLessonPartiallyOverlapsEnd() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 6, 10, 0);
        LocalDateTime end = start.plusHours(1);

        // Zajęcia zaczynające się w środku przedziału i kończące po nim
        Zajecia zajecia = new Zajecia(1L, start.plusMinutes(30), 45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        
        boolean free = tor.isWolny(start, end, List.of(zajecia));
        assertFalse(free, "Tor nie powinien być wolny, gdy zajęcia częściowo nakładają się na początek");
    }
}
