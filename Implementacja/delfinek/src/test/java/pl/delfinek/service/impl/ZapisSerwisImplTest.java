package pl.delfinek.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.delfinek.exception.BusinessException;
import pl.delfinek.exception.NotFoundException;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.Zapis;
import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.StatusZapisu;
import pl.delfinek.model.enums.TypZajec;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.repository.ZajeciaRepository;
import pl.delfinek.repository.ZapisRepository;
import pl.delfinek.service.PowiadomienieSerwis;

@ExtendWith(MockitoExtension.class)
class ZapisSerwisImplTest {

    @Mock
    private ZapisRepository zapisRepository;

    @Mock
    private ZajeciaRepository zajeciaRepository;

    @Mock
    private UzytkownikRepository uzytkownikRepository;

    @Mock
    private PowiadomienieSerwis powiadomienieSerwis;

    @InjectMocks
    private ZapisSerwisImpl service;

    @Test
    void shouldCreateEnrollment() {
        Klient klient = new Klient(1L, "Jan", "Kowalski", "jan@example.com",
                "pass", "123", null);
        Zajecia zajecia = new Zajecia(2L, LocalDateTime.now().plusDays(1), 45,
                TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);

        when(uzytkownikRepository.znajdzPoId(1L)).thenReturn(Optional.of(klient));
        when(zajeciaRepository.znajdzPoId(2L)).thenReturn(Optional.of(zajecia));
        when(zapisRepository.zapisz(any(Zapis.class))).thenAnswer(inv -> inv.getArgument(0));

        Zapis result = service.zapisz(1L, 2L);

        assertNotNull(result);
        assertEquals(klient, result.getKlient());
        assertEquals(zajecia, result.getZajecia());
        assertEquals(StatusZapisu.AKTYWNY, result.getStatus());
        verify(powiadomienieSerwis).powiadomONowymZapisie(result);
        verify(powiadomienieSerwis).powiadomONowymKursancie(zajecia, klient);
    }

    @Test
    void shouldThrowWhenNoFreePlace() {
        Zajecia zajecia = new Zajecia(2L, LocalDateTime.now().plusDays(1), 45,
                TypZajec.DZIECI_GR_A, 1, Cyklicznosc.JEDNORAZOWE);
        zajecia.dodajKursanta(new Klient(99L, "A", "B", "a@b.pl", "p", "123", null));

        when(uzytkownikRepository.znajdzPoId(1L)).thenReturn(Optional.of(mock(Klient.class)));
        when(zajeciaRepository.znajdzPoId(2L)).thenReturn(Optional.of(zajecia));

        assertThrows(BusinessException.class, () -> service.zapisz(1L, 2L));
    }

    @Test
    void shouldCancelEnrollment() {
        Zapis zapis = mock(Zapis.class);
        when(zapisRepository.znajdzPoId(5L)).thenReturn(Optional.of(zapis));
        when(zapis.getKlient()).thenReturn(new Klient(1L, "Jan", "Kowalski", "j@k.pl", "p", "123", null));
        Zajecia zajecia = new Zajecia(2L, LocalDateTime.now(), 45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        when(zapis.getZajecia()).thenReturn(zajecia);

        service.anuluj(5L, false);

        verify(zapis).anuluj();
        verify(zapisRepository).zapisz(zapis);
        verify(powiadomienieSerwis).wyslij(any(), anyList(), contains("Anulowano"));
    }

    @Test
    void shouldThrowWhenEnrollmentNotFound() {
        when(zapisRepository.znajdzPoId(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.anuluj(99L, false));
    }
}
