package pl.delfinek.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.Klient;
import pl.delfinek.model.Powiadomienie;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.enums.TypPowiadomienia;

class PowiadomienieRepositoryTest {

    private PowiadomienieRepository repository;
    private Uzytkownik odbiorca;

    @BeforeEach
    void setUp() {
        repository = new PowiadomienieRepository();
        odbiorca = new Klient(1L, "Jan", "Kowalski", "jan@example.com", 
                "pass", "123", LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldFindByOdbiorca() {
        Powiadomienie p1 = new Powiadomienie(null, "Tytuł1", "Treść1", 
                TypPowiadomienia.KOMUNIKAT_OGOLNY, odbiorca);
        Powiadomienie p2 = new Powiadomienie(null, "Tytuł2", "Treść2", 
                TypPowiadomienia.KOMUNIKAT_OGOLNY, odbiorca);
        repository.zapisz(p1);
        repository.zapisz(p2);
        
        List<Powiadomienie> found = repository.znajdzPoOdbiorcy(odbiorca);
        assertEquals(2, found.size());
    }

    @Test
    void shouldFindUnreadByOdbiorca() {
        Powiadomienie przeczytane = new Powiadomienie(null, "Tytuł1", "Treść1", 
                TypPowiadomienia.KOMUNIKAT_OGOLNY, odbiorca);
        przeczytane.oznaczJakoPrzeczytane();
        
        Powiadomienie nieprzeczytane = new Powiadomienie(null, "Tytuł2", "Treść2", 
                TypPowiadomienia.KOMUNIKAT_OGOLNY, odbiorca);
        
        repository.zapisz(przeczytane);
        repository.zapisz(nieprzeczytane);
        
        List<Powiadomienie> unread = repository.znajdzNieprzeczytanePoOdbiorcy(odbiorca);
        assertEquals(1, unread.size());
        assertFalse(unread.get(0).isPrzeczytane());
    }

    @Test
    void shouldReturnEmptyWhenNoNotifications() {
        List<Powiadomienie> found = repository.znajdzPoOdbiorcy(odbiorca);
        assertTrue(found.isEmpty());
    }
}
