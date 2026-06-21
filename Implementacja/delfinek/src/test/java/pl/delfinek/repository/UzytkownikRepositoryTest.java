package pl.delfinek.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.Administrator;
import pl.delfinek.model.Instruktor;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.enums.Rola;

class UzytkownikRepositoryTest {

    private UzytkownikRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UzytkownikRepository();
    }

    @Test
    void shouldFindByEmail() {
        Klient klient = new Klient(null, "Jan", "Kowalski", "jan@example.com", 
                "pass", "123", LocalDate.of(2000, 1, 1));
        repository.zapisz(klient);
        
        var found = repository.znajdzPoEmail("jan@example.com");
        assertTrue(found.isPresent());
        assertEquals("Jan", found.get().getImie());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        var found = repository.znajdzPoEmail("nonexistent@example.com");
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindByRole() {
        Klient klient = new Klient(null, "Jan", "Kowalski", "jan@example.com", 
                "pass", "123", LocalDate.of(2000, 1, 1));
        Instruktor instruktor = new Instruktor(null, "Piotr", "Nowak", "piotr@example.com", 
                "pass", "123", LocalDate.of(1990, 1, 1), "Opis");
        repository.zapisz(klient);
        repository.zapisz(instruktor);
        
        List<Uzytkownik> clients = repository.znajdzPoRoli(Rola.KLIENT);
        assertEquals(1, clients.size());
        assertTrue(clients.get(0) instanceof Klient);
        
        List<Uzytkownik> instructors = repository.znajdzPoRoli(Rola.INSTRUKTOR);
        assertEquals(1, instructors.size());
        assertTrue(instructors.get(0) instanceof Instruktor);
    }

    @Test
    void shouldFindByRoles() {
        Klient klient = new Klient(null, "Jan", "Kowalski", "jan@example.com", 
                "pass", "123", LocalDate.of(2000, 1, 1));
        Instruktor instruktor = new Instruktor(null, "Piotr", "Nowak", "piotr@example.com", 
                "pass", "123", LocalDate.of(1990, 1, 1), "Opis");
        Administrator admin = new Administrator(null, "Anna", "Kowalska", "admin@example.com", 
                "pass", "123", LocalDate.of(1985, 1, 1));
        repository.zapisz(klient);
        repository.zapisz(instruktor);
        repository.zapisz(admin);
        
        List<Uzytkownik> found = repository.znajdzPoRolach(List.of(Rola.KLIENT, Rola.INSTRUKTOR));
        assertEquals(2, found.size());
    }

    @Test
    void shouldSaveAllUserTypes() {
        Klient klient = new Klient(null, "Jan", "Kowalski", "jan@example.com", 
                "pass", "123", LocalDate.of(2000, 1, 1));
        Instruktor instruktor = new Instruktor(null, "Piotr", "Nowak", "piotr@example.com", 
                "pass", "123", LocalDate.of(1990, 1, 1), "Opis");
        Administrator admin = new Administrator(null, "Anna", "Kowalska", "admin@example.com", 
                "pass", "123", LocalDate.of(1985, 1, 1));
        
        repository.zapisz(klient);
        repository.zapisz(instruktor);
        repository.zapisz(admin);
        
        assertEquals(3, repository.znajdzWszystkie().size());
    }
}
