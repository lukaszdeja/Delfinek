package pl.delfinek.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.Instruktor;
import pl.delfinek.model.Tor;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.TypZajec;

class ZajeciaRepositoryTest {

    private ZajeciaRepository repository;
    private Instruktor instruktor;
    private Tor tor;

    @BeforeEach
    void setUp() {
        repository = new ZajeciaRepository();
        instruktor = new Instruktor(1L, "Piotr", "Nowak", "piotr@example.com", 
                "pass", "123", LocalDate.of(1990, 1, 1), "Opis");
        tor = new Tor(1L, 1, 25, 1.8);
    }

    @Test
    void shouldFindByInstructorInRange() {
        Zajecia zajecia1 = new Zajecia(null, LocalDateTime.of(2026, Month.JULY, 6, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        zajecia1.setInstruktor(instruktor);
        
        Zajecia zajecia2 = new Zajecia(null, LocalDateTime.of(2026, Month.JULY, 6, 12, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        zajecia2.setInstruktor(instruktor);
        
        Zajecia zajecia3 = new Zajecia(null, LocalDateTime.of(2026, Month.JULY, 7, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        zajecia3.setInstruktor(instruktor);
        
        repository.zapisz(zajecia1);
        repository.zapisz(zajecia2);
        repository.zapisz(zajecia3);
        
        LocalDateTime from = LocalDateTime.of(2026, Month.JULY, 6, 8, 0);
        LocalDateTime to = LocalDateTime.of(2026, Month.JULY, 6, 18, 0);
        
        List<Zajecia> found = repository.znajdzPoInstruktorzeWPrzedziale(1L, from, to);
        assertEquals(2, found.size());
    }

    @Test
    void shouldFindInRange() {
        Zajecia zajecia1 = new Zajecia(null, LocalDateTime.of(2026, Month.JULY, 6, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        Zajecia zajecia2 = new Zajecia(null, LocalDateTime.of(2026, Month.JULY, 7, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        Zajecia zajecia3 = new Zajecia(null, LocalDateTime.of(2026, Month.JULY, 8, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        
        repository.zapisz(zajecia1);
        repository.zapisz(zajecia2);
        repository.zapisz(zajecia3);
        
        LocalDateTime from = LocalDateTime.of(2026, Month.JULY, 6, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, Month.JULY, 7, 23, 59);
        
        List<Zajecia> found = repository.znajdzWPrzedziale(from, to);
        assertEquals(2, found.size());
    }

    @Test
    void shouldFindTorConflicts() {
        Zajecia existing = new Zajecia(1L, LocalDateTime.of(2026, Month.JULY, 6, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        existing.setTor(tor);
        repository.zapisz(existing);
        
        // Kolidujące zajęcia na tym samym torze
        LocalDateTime start = LocalDateTime.of(2026, Month.JULY, 6, 10, 20);
        LocalDateTime end = start.plusMinutes(45);
        List<Zajecia> conflicts = repository.znajdzKonfliktyToru(tor, start, end, null);
        assertEquals(1, conflicts.size());
        
        // Nie kolidujące zajęcia
        LocalDateTime start2 = LocalDateTime.of(2026, Month.JULY, 6, 11, 0);
        LocalDateTime end2 = start2.plusMinutes(45);
        List<Zajecia> noConflicts = repository.znajdzKonfliktyToru(tor, start2, end2, null);
        assertEquals(0, noConflicts.size());
    }

    @Test
    void shouldFindInstructorConflicts() {
        Zajecia existing = new Zajecia(1L, LocalDateTime.of(2026, Month.JULY, 6, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        existing.setInstruktor(instruktor);
        repository.zapisz(existing);
        
        LocalDateTime start = LocalDateTime.of(2026, Month.JULY, 6, 10, 20);
        LocalDateTime end = start.plusMinutes(45);
        List<Zajecia> conflicts = repository.znajdzKonfliktyInstruktora(instruktor, start, end, null);
        assertEquals(1, conflicts.size());
    }

    @Test
    void shouldExcludeCurrentLessonWhenFindingConflicts() {
        Zajecia existing = new Zajecia(1L, LocalDateTime.of(2026, Month.JULY, 6, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        existing.setTor(tor);
        repository.zapisz(existing);
        
        LocalDateTime start = LocalDateTime.of(2026, Month.JULY, 6, 10, 20);
        LocalDateTime end = start.plusMinutes(45);
        List<Zajecia> conflicts = repository.znajdzKonfliktyToru(tor, start, end, 1L);
        assertEquals(0, conflicts.size());
    }

    @Test
    void shouldFindByTor() {
        Zajecia zajecia1 = new Zajecia(null, LocalDateTime.of(2026, Month.JULY, 6, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        zajecia1.setTor(tor);
        Zajecia zajecia2 = new Zajecia(null, LocalDateTime.of(2026, Month.JULY, 7, 10, 0), 
                45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        zajecia2.setTor(tor);
        
        repository.zapisz(zajecia1);
        repository.zapisz(zajecia2);
        
        List<Zajecia> found = repository.znajdzPoTorze(tor);
        assertEquals(2, found.size());
    }
}
