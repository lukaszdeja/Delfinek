package pl.delfinek.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.delfinek.exception.ValidationException;
import pl.delfinek.model.Instruktor;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Powiadomienie;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.Zapis;
import pl.delfinek.model.enums.Rola;
import pl.delfinek.model.enums.TypPowiadomienia;
import pl.delfinek.repository.PowiadomienieRepository;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.service.MailSerwis;

@ExtendWith(MockitoExtension.class)
class PowiadomienieSerwisImplTest {

    @Mock
    private PowiadomienieRepository powiadomienieRepository;

    @Mock
    private UzytkownikRepository uzytkownikRepository;

    @Mock
    private MailSerwis mailSerwis;

    @InjectMocks
    private PowiadomienieSerwisImpl service;

    @Test
    void shouldSendNotificationToUsers() {
        Uzytkownik u1 = mock(Uzytkownik.class);
        Uzytkownik u2 = mock(Uzytkownik.class);
        service.wyslij(TypPowiadomienia.KOMUNIKAT_OGOLNY, List.of(u1, u2), "test");

        verify(powiadomienieRepository, times(2)).zapisz(any(Powiadomienie.class));
        verify(u1).dodajPowiadomienie(any());
        verify(u2).dodajPowiadomienie(any());
    }

    @Test
    void shouldSendMassMessageToAllClients() {
        Uzytkownik k1 = mock(Klient.class);
        Uzytkownik k2 = mock(Klient.class);
        when(k1.getEmail()).thenReturn("a@b.pl");
        when(k2.getEmail()).thenReturn("c@d.pl");
        when(uzytkownikRepository.znajdzPoRolach(List.of(Rola.KLIENT))).thenReturn(List.of(k1, k2));

        int count = service.wyslijKomunikat("Ważny komunikat", List.of(Rola.KLIENT));

        assertEquals(2, count);
        verify(powiadomienieRepository, times(2)).zapisz(any(Powiadomienie.class));
        verify(mailSerwis).wyslijEmailMasowy(anyList(), anyString(), anyString());
    }

    @Test
    void shouldThrowIfMessageEmpty() {
        assertThrows(ValidationException.class,
                () -> service.wyslijKomunikat("   ", List.of(Rola.KLIENT)));
    }

    @Test
    void shouldNotifyAboutScheduleChange() {
        Zajecia zajecia = mock(Zajecia.class);
        Instruktor instruktor = mock(Instruktor.class);
        Klient klient = mock(Klient.class);
        when(zajecia.getInstruktor()).thenReturn(instruktor);
        when(zajecia.getListaUczestnikow()).thenReturn(List.of(klient));
        when(instruktor.getEmail()).thenReturn("i@x.pl");
        when(klient.getEmail()).thenReturn("k@x.pl");

        service.powiadomOZmianieHarmonogramu(zajecia);

        verify(powiadomienieRepository, times(2)).zapisz(any(Powiadomienie.class));
        verify(mailSerwis, times(2)).wyslijEmail(anyString(), anyString(), anyString());
    }
}
