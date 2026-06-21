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
