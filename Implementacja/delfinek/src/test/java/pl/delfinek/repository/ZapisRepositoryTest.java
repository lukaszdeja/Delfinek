package pl.delfinek.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.Klient;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.Zapis;
import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.StatusZapisu;
import pl.delfinek.model.enums.TypZajec;

class ZapisRepositoryTest {

    private ZapisRepository repository;
    private Klient klient;
    private Zajecia zajecia;

    @BeforeEach
    void setUp() {
        repository = new ZapisRepository();
        klient = new Klient(1L, "Jan", "Kowalski", "jan@example.com", 
                "pass", "123", LocalDate.of(2000, 1, 1));
        zajecia = new Zajecia(1L, LocalDateTime.now().plusDays(1), 45, 
                TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
    }

    @Test
    void shouldFindActiveByZajecia() {
        Zapis aktywny = new Zapis(null, klient, zajecia);
        Zapis anulowany = new Zapis(null, klient, zajecia);
        anulowany.anuluj();
        
        repository.zapisz(aktywny);
        repository.zapisz(anulowany);
        
        var found = repository.znajdzAktywneZajeciaPoZajeciach(zajecia);
        assertEquals(1, found.size());
        assertEquals(StatusZapisu.AKTYWNY, found.get(0).getStatus());
    }

    @Test
    void shouldFindByKlient() {
        Zapis zapis1 = new Zapis(null, klient, zajecia);
        Zapis zapis2 = new Zapis(null, klient, zajecia);
        repository.zapisz(zapis1);
        repository.zapisz(zapis2);
        
        var found = repository.znajdzPoKliencie(klient);
        assertEquals(2, found.size());
    }

    @Test
    void shouldFindActiveKursants() {
        Klient klient1 = new Klient(1L, "Jan", "Kowalski", "jan@example.com", 
                "pass", "123", LocalDate.of(2000, 1, 1));
        Klient klient2 = new Klient(2L, "Anna", "Nowak", "anna@example.com", 
                "pass", "123", LocalDate.of(2000, 1, 1));
        
        Zapis aktywny = new Zapis(null, klient1, zajecia);
        Zapis anulowany = new Zapis(null, klient2, zajecia);
        anulowany.anuluj();
        
        repository.zapisz(aktywny);
        repository.zapisz(anulowany);
        
        var found = repository.znajdzAktywnychKursantowZajec(zajecia);
        assertEquals(1, found.size());
        assertEquals(klient1.getId(), found.get(0).getId());
    }
}
