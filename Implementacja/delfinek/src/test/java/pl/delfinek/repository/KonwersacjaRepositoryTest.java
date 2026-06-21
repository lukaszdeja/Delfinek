package pl.delfinek.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.Instruktor;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Konwersacja;

class KonwersacjaRepositoryTest {

    private KonwersacjaRepository repository;
    private Klient klient;
    private Instruktor instruktor;

    @BeforeEach
    void setUp() {
        repository = new KonwersacjaRepository();
        klient = new Klient(1L, "Jan", "Kowalski", "jan@example.com", 
                "pass", "123", LocalDate.of(2000, 1, 1));
        instruktor = new Instruktor(2L, "Piotr", "Nowak", "piotr@example.com", 
                "pass", "123", LocalDate.of(1990, 1, 1), "Opis");
    }

    @Test
    void shouldFindConversationBetweenUsers() {
        Konwersacja konw = new Konwersacja(null, klient, instruktor);
        repository.zapisz(konw);
        
        var found = repository.znajdzPomiedzyUzytkownikami(1L, 2L);
        assertTrue(found.isPresent());
        
        var foundReverse = repository.znajdzPomiedzyUzytkownikami(2L, 1L);
        assertTrue(foundReverse.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenNoConversation() {
        var found = repository.znajdzPomiedzyUzytkownikami(1L, 3L);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldNotFindWhenOneUserNotFound() {
        Konwersacja konw = new Konwersacja(null, klient, instruktor);
        repository.zapisz(konw);
        
        var found = repository.znajdzPomiedzyUzytkownikami(1L, 99L);
        assertTrue(found.isEmpty());
    }
}
