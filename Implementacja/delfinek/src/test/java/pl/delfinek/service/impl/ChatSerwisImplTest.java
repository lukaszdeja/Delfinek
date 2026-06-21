package pl.delfinek.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.delfinek.exception.NotFoundException;
import pl.delfinek.model.Konwersacja;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.Wiadomosc;
import pl.delfinek.model.enums.TypPowiadomienia;
import pl.delfinek.repository.KonwersacjaRepository;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.service.PowiadomienieSerwis;

@ExtendWith(MockitoExtension.class)
class ChatSerwisImplTest {

    @Mock
    private KonwersacjaRepository konwersacjaRepository;

    @Mock
    private UzytkownikRepository uzytkownikRepository;

    @Mock
    private PowiadomienieSerwis powiadomienieSerwis;

    @InjectMocks
    private ChatSerwisImpl service;

    @Test
    void shouldGetExistingConversation() {
        Konwersacja konw = mock(Konwersacja.class);
        when(konwersacjaRepository.znajdzPomiedzyUzytkownikami(1L, 2L)).thenReturn(Optional.of(konw));
        assertEquals(konw, service.pobierzLubUtworzKonwersacje(1L, 2L));
    }

    @Test
    void shouldCreateNewConversationIfNotExists() {
        Uzytkownik u1 = mock(Uzytkownik.class);
        Uzytkownik u2 = mock(Uzytkownik.class);
        when(uzytkownikRepository.znajdzPoId(1L)).thenReturn(Optional.of(u1));
        when(uzytkownikRepository.znajdzPoId(2L)).thenReturn(Optional.of(u2));
        when(konwersacjaRepository.znajdzPomiedzyUzytkownikami(1L, 2L)).thenReturn(Optional.empty());
        when(konwersacjaRepository.zapisz(any(Konwersacja.class))).thenAnswer(inv -> inv.getArgument(0));

        Konwersacja result = service.pobierzLubUtworzKonwersacje(1L, 2L);
        assertNotNull(result);
        verify(konwersacjaRepository).zapisz(any(Konwersacja.class));
    }

    @Test
    void shouldSendMessage() {
        Konwersacja konw = mock(Konwersacja.class);
        Uzytkownik nadawca = mock(Uzytkownik.class);
        Uzytkownik drugi = mock(Uzytkownik.class);
        when(nadawca.getPelneImie()).thenReturn("Jan Kowalski");

        when(konwersacjaRepository.znajdzPoId(1L)).thenReturn(Optional.of(konw));
        when(uzytkownikRepository.znajdzPoId(2L)).thenReturn(Optional.of(nadawca));
        when(konw.getUczestnik1()).thenReturn(nadawca);
        when(konw.getUczestnik2()).thenReturn(drugi);
        when(konw.wyslijWiadomosc(any(), anyString(), isNull())).thenReturn(mock(Wiadomosc.class));

        Wiadomosc result = service.wyslijWiadomosc(1L, 2L, "Hello");
        assertNotNull(result);
        verify(powiadomienieSerwis).wyslij(eq(TypPowiadomienia.NOWA_WIADOMOSC), anyList(), anyString());
        verify(konwersacjaRepository).zapisz(konw);
    }

    @Test
    void shouldThrowWhenConversationNotFound() {
        when(konwersacjaRepository.znajdzPoId(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.wyslijWiadomosc(99L, 1L, "test"));
    }
}
