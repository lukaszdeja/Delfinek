#!/bin/bash

# Utwórz katalog dla testów repozytoriów
mkdir -p src/test/java/pl/delfinek/repository

# 1. InMemoryRepositoryTest.java
cat > src/test/java/pl/delfinek/repository/InMemoryRepositoryTest.java << 'EOF'
package pl.delfinek.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.enums.Rola;

class InMemoryRepositoryTest {

    private TestRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TestRepository();
    }

    @Test
    void shouldSaveNewEntity() {
        TestEntity entity = new TestEntity(null, "Test");
        TestEntity saved = repository.zapisz(entity);
        
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getId());
        assertEquals("Test", saved.getName());
    }

    @Test
    void shouldUpdateExistingEntity() {
        TestEntity entity = new TestEntity(null, "Initial");
        TestEntity saved = repository.zapisz(entity);
        
        saved.setName("Updated");
        TestEntity updated = repository.zapisz(saved);
        
        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated", updated.getName());
    }

    @Test
    void shouldFindById() {
        TestEntity entity = new TestEntity(null, "Test");
        TestEntity saved = repository.zapisz(entity);
        
        Optional<TestEntity> found = repository.znajdzPoId(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        Optional<TestEntity> found = repository.znajdzPoId(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAll() {
        repository.zapisz(new TestEntity(null, "First"));
        repository.zapisz(new TestEntity(null, "Second"));
        
        List<TestEntity> all = repository.znajdzWszystkie();
        assertEquals(2, all.size());
    }

    @Test
    void shouldDelete() {
        TestEntity entity = new TestEntity(null, "ToDelete");
        TestEntity saved = repository.zapisz(entity);
        
        repository.usun(saved.getId());
        Optional<TestEntity> found = repository.znajdzPoId(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldCheckExistence() {
        TestEntity entity = new TestEntity(null, "Test");
        TestEntity saved = repository.zapisz(entity);
        
        assertTrue(repository.istnieje(saved.getId()));
        assertFalse(repository.istnieje(999L));
    }

    // Helper classes for testing
    private static class TestEntity {
        private Long id;
        private String name;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    private static class TestRepository extends InMemoryRepository<TestEntity> {
        @Override
        protected Long pobierzId(TestEntity encja) {
            return encja.getId();
        }

        @Override
        protected void ustawId(TestEntity encja, Long id) {
            encja.setId(id);
        }
    }
}
EOF

# 2. UzytkownikRepositoryTest.java
cat > src/test/java/pl/delfinek/repository/UzytkownikRepositoryTest.java << 'EOF'
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
EOF

# 3. ZajeciaRepositoryTest.java
cat > src/test/java/pl/delfinek/repository/ZajeciaRepositoryTest.java << 'EOF'
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
EOF

# 4. ZapisRepositoryTest.java
cat > src/test/java/pl/delfinek/repository/ZapisRepositoryTest.java << 'EOF'
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
EOF

# 5. TorRepositoryTest.java
cat > src/test/java/pl/delfinek/repository/TorRepositoryTest.java << 'EOF'
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
EOF

# 6. KonwersacjaRepositoryTest.java
cat > src/test/java/pl/delfinek/repository/KonwersacjaRepositoryTest.java << 'EOF'
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
EOF

# 7. PowiadomienieRepositoryTest.java
cat > src/test/java/pl/delfinek/repository/PowiadomienieRepositoryTest.java << 'EOF'
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
EOF

echo "✅ Wszystkie testy repozytoriów zostały utworzone w src/test/java/pl/delfinek/repository/"
