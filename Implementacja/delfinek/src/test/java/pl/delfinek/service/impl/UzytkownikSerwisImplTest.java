package pl.delfinek.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.delfinek.dto.RegistrationDTO;
import pl.delfinek.exception.NotFoundException;
import pl.delfinek.exception.ValidationException;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.enums.Rola;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.service.MailSerwis;

@ExtendWith(MockitoExtension.class)
class UzytkownikSerwisImplTest {

    @Mock
    private UzytkownikRepository uzytkownikRepository;

    @Mock
    private MailSerwis mailSerwis;

    @InjectMocks
    private UzytkownikSerwisImpl service;

    private RegistrationDTO validDto;

    @BeforeEach
    void setUp() {
        validDto = new RegistrationDTO("Jan", "Kowalski", "jan@example.com",
                "Haslo123!", "123456789", LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldRegisterClientSuccessfully() {
        when(uzytkownikRepository.znajdzPoEmail(anyString())).thenReturn(Optional.empty());
        when(uzytkownikRepository.zapisz(any(Klient.class))).thenAnswer(inv -> {
            Klient k = inv.getArgument(0);
            k.setId(1L);
            return k;
        });

        Klient result = service.rejestruj(validDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jan", result.getImie());
        assertEquals(Rola.KLIENT, result.getRola());
        verify(mailSerwis).wyslijEmail(eq("jan@example.com"), contains("Witamy"), anyString());
        verify(uzytkownikRepository).zapisz(any(Klient.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(uzytkownikRepository.znajdzPoEmail("jan@example.com"))
                .thenReturn(Optional.of(mock(Uzytkownik.class)));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.rejestruj(validDto));
        assertTrue(ex.getMessage().contains("już istnieje"));
    }

    @Test
    void shouldThrowWhenPasswordInvalid() {
        validDto.setHaslo("weak");
        assertThrows(ValidationException.class, () -> service.rejestruj(validDto));
    }

    @Test
    void shouldFindUserById() {
        Uzytkownik user = mock(Uzytkownik.class);
        when(uzytkownikRepository.znajdzPoId(1L)).thenReturn(Optional.of(user));
        assertEquals(user, service.znajdzPoId(1L));
    }

    @Test
    void shouldThrowNotFoundWhenUserMissing() {
        when(uzytkownikRepository.znajdzPoId(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.znajdzPoId(99L));
    }

    @Test
    void shouldChangeRole() {
        Uzytkownik user = mock(Uzytkownik.class);
        when(uzytkownikRepository.znajdzPoId(1L)).thenReturn(Optional.of(user));
        service.zmienRole(1L, Rola.INSTRUKTOR);
        verify(user).setRola(Rola.INSTRUKTOR);
        verify(uzytkownikRepository).zapisz(user);
    }

    @Test
    void shouldResetPassword() {
        Uzytkownik user = mock(Uzytkownik.class);
        when(user.getEmail()).thenReturn("jan@example.com");
        when(uzytkownikRepository.znajdzPoEmail("jan@example.com")).thenReturn(Optional.of(user));
        service.resetujHaslo("jan@example.com");
        verify(mailSerwis).wyslijEmail(eq("jan@example.com"), contains("Reset"), anyString());
    }
}
