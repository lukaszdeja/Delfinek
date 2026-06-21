package pl.delfinek.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.delfinek.exception.NotFoundException;
import pl.delfinek.model.Tor;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.enums.StatusToru;
import pl.delfinek.repository.TorRepository;
import pl.delfinek.repository.ZajeciaRepository;

@ExtendWith(MockitoExtension.class)
class TorSerwisImplTest {

    @Mock
    private TorRepository torRepository;

    @Mock
    private ZajeciaRepository zajeciaRepository;

    @InjectMocks
    private TorSerwisImpl service;

    @Test
    void shouldCheckAvailabilityWhenFree() {
        Tor tor = new Tor(1L, 1, 25, 1.8);
        when(torRepository.znajdzPoId(1L)).thenReturn(Optional.of(tor));
        when(zajeciaRepository.znajdzPoTorze(tor)).thenReturn(List.of());

        boolean available = service.sprawdzDostepnosc(1L,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertTrue(available);
    }

    @Test
    void shouldReturnFalseWhenTorInRepair() {
        Tor tor = new Tor(1L, 1, 25, 1.8);
        tor.zglosRemont(LocalDate.now(), LocalDate.now().plusDays(2));
        when(torRepository.znajdzPoId(1L)).thenReturn(Optional.of(tor));
        when(zajeciaRepository.znajdzPoTorze(tor)).thenReturn(List.of());

        boolean available = service.sprawdzDostepnosc(1L,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertFalse(available);
    }

    @Test
    void shouldReportRepair() {
        Tor tor = new Tor(1L, 1, 25, 1.8);
        when(torRepository.znajdzPoId(1L)).thenReturn(Optional.of(tor));
        service.zglosRemont(1L, LocalDate.now(), LocalDate.now().plusDays(5));
        assertEquals(StatusToru.REMONT, tor.getStatus());
        verify(torRepository).zapisz(tor);
    }

    @Test
    void shouldFinishRepair() {
        Tor tor = new Tor(1L, 1, 25, 1.8);
        tor.zglosRemont(LocalDate.now(), LocalDate.now().plusDays(5));
        when(torRepository.znajdzPoId(1L)).thenReturn(Optional.of(tor));
        service.zakonczRemont(1L);
        assertEquals(StatusToru.WOLNY, tor.getStatus());
        verify(torRepository).zapisz(tor);
    }
}
