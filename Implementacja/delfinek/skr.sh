#!/bin/bash

mkdir -p src/test/java/pl/delfinek/model

# ZajeciaTest
cat > src/test/java/pl/delfinek/model/ZajeciaTest.java << 'EOF'
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
EOF

# ZapisTest
cat > src/test/java/pl/delfinek/model/ZapisTest.java << 'EOF'
package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.StatusZapisu;
import pl.delfinek.model.enums.TypZajec;

class ZapisTest {

    private Klient klient;
    private Zajecia zajecia;
    private Zapis zapis;

    @BeforeEach
    void setUp() {
        klient = new Klient(1L, "Jan", "Kowalski", "jan@example.com", "pass", "123", null);
        zajecia = new Zajecia(1L, LocalDateTime.now().plusDays(1), 45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        zapis = new Zapis(1L, klient, zajecia);
    }

    @Test
    void shouldCreateActiveEnrollment() {
        assertNotNull(zapis.getDataZapisu());
        assertEquals(StatusZapisu.AKTYWNY, zapis.getStatus());
        assertEquals(klient, zapis.getKlient());
        assertEquals(zajecia, zapis.getZajecia());
    }

    @Test
    void shouldCancelEnrollment() {
        zapis.anuluj();
        assertEquals(StatusZapisu.ANULOWANY, zapis.getStatus());
        // sprawdzenie, czy klient został usunięty z zajęć
        assertFalse(zajecia.getListaUczestnikow().contains(klient));
    }

    @Test
    void shouldThrowWhenAlreadyCancelled() {
        zapis.anuluj();
        assertThrows(IllegalStateException.class, () -> zapis.anuluj());
    }
}
EOF

# TorTest
cat > src/test/java/pl/delfinek/model/TorTest.java << 'EOF'
package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.StatusToru;
import pl.delfinek.model.enums.TypZajec;

class TorTest {

    private Tor tor;
    private Zajecia zajecia1;
    private Zajecia zajecia2;

    @BeforeEach
    void setUp() {
        tor = new Tor(1L, 1, 25, 1.8);
        zajecia1 = new Zajecia(1L, LocalDateTime.of(2026, 7, 10, 10, 0),
                60, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        zajecia2 = new Zajecia(2L, LocalDateTime.of(2026, 7, 10, 11, 0),
                60, TypZajec.DZIECI_GR_B, 10, Cyklicznosc.JEDNORAZOWE);
    }

    @Test
    void shouldBeFreeWhenNoCollision() {
        assertTrue(tor.isWolny(LocalDateTime.of(2026, 7, 10, 9, 0),
                LocalDateTime.of(2026, 7, 10, 10, 0), List.of()));
        assertTrue(tor.isWolny(LocalDateTime.of(2026, 7, 10, 10, 0),
                LocalDateTime.of(2026, 7, 10, 11, 0), List.of(zajecia1))); // koniec na starcie
    }

    @Test
    void shouldDetectCollision() {
        List<Zajecia> lista = List.of(zajecia1);
        assertFalse(tor.isWolny(LocalDateTime.of(2026, 7, 10, 9, 30),
                LocalDateTime.of(2026, 7, 10, 10, 30), lista));
        assertFalse(tor.isWolny(LocalDateTime.of(2026, 7, 10, 10, 0),
                LocalDateTime.of(2026, 7, 10, 11, 0), lista));
    }

    @Test
    void shouldBeBusyWhenRepair() {
        tor.zglosRemont(LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12));
        assertFalse(tor.isWolny(LocalDateTime.of(2026, 7, 11, 10, 0),
                LocalDateTime.of(2026, 7, 11, 11, 0), List.of()));
    }

    @Test
    void shouldSetRepairStatus() {
        tor.zglosRemont(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 10));
        assertEquals(StatusToru.REMONT, tor.getStatus());
        assertNotNull(tor.getUwagi());
        assertTrue(tor.getUwagi().contains("Remont"));
    }

    @Test
    void shouldFinishRepair() {
        tor.zglosRemont(LocalDate.now(), LocalDate.now().plusDays(1));
        tor.zakonczRemont();
        assertEquals(StatusToru.WOLNY, tor.getStatus());
        assertNull(tor.getUwagi());
    }

    @Test
    void shouldThrowWhenInvalidRepairRange() {
        assertThrows(IllegalArgumentException.class,
                () -> tor.zglosRemont(LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 1)));
    }
}
EOF

# KonwersacjaTest
cat > src/test/java/pl/delfinek/model/KonwersacjaTest.java << 'EOF'
package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KonwersacjaTest {

    private Uzytkownik u1;
    private Uzytkownik u2;
    private Konwersacja konwersacja;

    @BeforeEach
    void setUp() {
        u1 = new Klient(1L, "Jan", "Kowalski", "jan@example.com", "pass", "123", null);
        u2 = new Instruktor(2L, "Piotr", "Nowak", "piotr@example.com", "pass", "456", null, "opis");
        konwersacja = new Konwersacja(1L, u1, u2);
    }

    @Test
    void shouldCreateConversation() {
        assertNotNull(konwersacja.getDataUtworzenia());
        assertEquals(u1, konwersacja.getUczestnik1());
        assertEquals(u2, konwersacja.getUczestnik2());
        assertTrue(konwersacja.getWiadomosci().isEmpty());
    }

    @Test
    void shouldSendMessage() {
        Wiadomosc w = konwersacja.wyslijWiadomosc(u1, "Hello", 1L);
        assertEquals(1, konwersacja.getWiadomosci().size());
        assertEquals("Hello", w.getTresc());
        assertEquals(u1, w.getNadawca());
    }

    @Test
    void shouldThrowWhenEmptyMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> konwersacja.wyslijWiadomosc(u1, " ", 2L));
        assertThrows(IllegalArgumentException.class,
                () -> konwersacja.wyslijWiadomosc(u1, null, 2L));
    }

    @Test
    void shouldCheckParticipants() {
        assertTrue(konwersacja.dotyczyUczestnikow(1L, 2L));
        assertTrue(konwersacja.dotyczyUczestnikow(2L, 1L));
        assertFalse(konwersacja.dotyczyUczestnikow(1L, 3L));
        assertFalse(konwersacja.dotyczyUczestnikow(3L, 2L));
    }
}
EOF

# PowiadomienieTest
cat > src/test/java/pl/delfinek/model/PowiadomienieTest.java << 'EOF'
package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.TypPowiadomienia;

class PowiadomienieTest {

    private Uzytkownik odbiorca;
    private Powiadomienie powiadomienie;

    @BeforeEach
    void setUp() {
        odbiorca = new Klient(1L, "Jan", "Kowalski", "jan@example.com", "pass", "123", null);
        powiadomienie = new Powiadomienie(1L, "Tytul", "Tresc", TypPowiadomienia.KOMUNIKAT_OGOLNY, odbiorca);
    }

    @Test
    void shouldCreateNotification() {
        assertNotNull(powiadomienie.getDataCzas());
        assertFalse(powiadomienie.isPrzeczytane());
        assertEquals("Tytul", powiadomienie.getTytul());
        assertEquals("Tresc", powiadomienie.getTresc());
        assertEquals(odbiorca, powiadomienie.getOdbiorca());
    }

    @Test
    void shouldMarkAsRead() {
        powiadomienie.oznaczJakoPrzeczytane();
        assertTrue(powiadomienie.isPrzeczytane());
    }
}
EOF

# KlientTest (dodatkowo dla specyficznych metod)
cat > src/test/java/pl/delfinek/model/KlientTest.java << 'EOF'
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
EOF

# InstruktorTest (dla edycji opisu)
cat > src/test/java/pl/delfinek/model/InstruktorTest.java << 'EOF'
package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstruktorTest {

    private Instruktor instruktor;

    @BeforeEach
    void setUp() {
        instruktor = new Instruktor(1L, "Piotr", "Nowak", "piotr@example.com", "pass",
                "123", LocalDate.of(1990, 1, 1), "Początkowy opis");
    }

    @Test
    void shouldEditDescription() {
        instruktor.edytujSwojOpis("Nowy opis");
        assertEquals("Nowy opis", instruktor.getOpis());
    }

    @Test
    void shouldHaveCorrectRole() {
        assertEquals(pl.delfinek.model.enums.Rola.INSTRUKTOR, instruktor.getRola());
    }
}
EOF

# UzytkownikTest (abstrakcyjna – testujemy wspólne metody przez konkretną klasę)
cat > src/test/java/pl/delfinek/model/UzytkownikTest.java << 'EOF'
package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.enums.Rola;

class UzytkownikTest {

    private Klient uzytkownik; // używamy Klient jako konkretnej implementacji

    @BeforeEach
    void setUp() {
        uzytkownik = new Klient(1L, "Jan", "Kowalski", "jan@example.com", "oldPass", "123", LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldLoginSuccessfully() {
        assertTrue(uzytkownik.zalogujSie("jan@example.com", "oldPass"));
    }

    @Test
    void shouldLoginFailWrongPassword() {
        assertFalse(uzytkownik.zalogujSie("jan@example.com", "wrong"));
    }

    @Test
    void shouldLoginFailWrongEmail() {
        assertFalse(uzytkownik.zalogujSie("wrong@example.com", "oldPass"));
    }

    @Test
    void shouldChangePassword() {
        uzytkownik.zmienHaslo("oldPass", "newPass123!");
        assertTrue(uzytkownik.zalogujSie("jan@example.com", "newPass123!"));
    }

    @Test
    void shouldThrowWhenOldPasswordIncorrect() {
        assertThrows(IllegalArgumentException.class,
                () -> uzytkownik.zmienHaslo("wrong", "newPass"));
    }

    @Test
    void shouldThrowWhenNewPasswordEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> uzytkownik.zmienHaslo("oldPass", ""));
    }

    @Test
    void shouldResetPassword() {
        uzytkownik.resetujHaslo("newResetPass");
        assertTrue(uzytkownik.zalogujSie("jan@example.com", "newResetPass"));
    }

    @Test
    void shouldThrowWhenResetPasswordEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> uzytkownik.resetujHaslo(""));
    }

    @Test
    void shouldEditProfile() {
        uzytkownik.edytujProfil("Janusz", "Nowak", "999", LocalDate.of(1990, 5, 5));
        assertEquals("Janusz", uzytkownik.getImie());
        assertEquals("Nowak", uzytkownik.getNazwisko());
        assertEquals("999", uzytkownik.getNrTelefonu());
        assertEquals(LocalDate.of(1990, 5, 5), uzytkownik.getDataUrodzenia());
    }

    @Test
    void shouldNotOverrideWithNulls() {
        String originalName = uzytkownik.getImie();
        uzytkownik.edytujProfil(null, null, null, null);
        assertEquals(originalName, uzytkownik.getImie());
    }
}
EOF

echo "✅ Wszystkie testy modeli zostały utworzone w src/test/java/pl/delfinek/model/"
