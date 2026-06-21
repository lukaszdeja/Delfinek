package pl.delfinek.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.Tor;
import pl.delfinek.model.enums.StatusToru;

class TorRepositoryTest {

    private TorRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TorRepository();
    }

    @Test
    void shouldFindByNumber() {
        Tor tor = new Tor(null, 1, 25, 1.8);
        repository.zapisz(tor);
        
        var found = repository.znajdzPoNumerze(1);
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getNumer());
    }

    @Test
    void shouldReturnEmptyWhenNumberNotFound() {
        var found = repository.znajdzPoNumerze(99);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllFree() {
        Tor wolny = new Tor(null, 1, 25, 1.8);
        Tor zajety = new Tor(null, 2, 25, 1.8);
        zajety.setStatus(StatusToru.ZAJETY);
        
        repository.zapisz(wolny);
        repository.zapisz(zajety);
        
        List<Tor> free = repository.znajdzWszystkieWolne();
        assertEquals(1, free.size());
        assertEquals(1, free.get(0).getNumer());
    }

    @Test
    void shouldNotIncludeRepairingTorsInFree() {
        Tor wolny = new Tor(null, 1, 25, 1.8);
        Tor wRemoncie = new Tor(null, 2, 25, 1.8);
        wRemoncie.setStatus(StatusToru.REMONT);
        
        repository.zapisz(wolny);
        repository.zapisz(wRemoncie);
        
        List<Tor> free = repository.znajdzWszystkieWolne();
        assertEquals(1, free.size());
        assertEquals(1, free.get(0).getNumer());
    }
}
