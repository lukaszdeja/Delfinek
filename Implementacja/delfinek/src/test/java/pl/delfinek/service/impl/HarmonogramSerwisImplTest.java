package pl.delfinek.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.delfinek.dto.ZajeciaDTO;
import pl.delfinek.exception.ConflictException;
import pl.delfinek.exception.NotFoundException;
import pl.delfinek.exception.ValidationException;
import pl.delfinek.model.Instruktor;
import pl.delfinek.model.Tor;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.TypZajec;
import pl.delfinek.repository.TorRepository;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.repository.ZajeciaRepository;
import pl.delfinek.repository.ZapisRepository;
import pl.delfinek.service.PowiadomienieSerwis;
import pl.delfinek.service.TorSerwis;

@ExtendWith(MockitoExtension.class)
class HarmonogramSerwisImplTest {

    @Mock
    private ZajeciaRepository zajeciaRepository;

    @Mock
    private ZapisRepository zapisRepository;

    @Mock
    private TorRepository torRepository;

    @Mock
    private UzytkownikRepository uzytkownikRepository;

    @Mock
    private TorSerwis torSerwis;

    @Mock
    private PowiadomienieSerwis powiadomienieSerwis;

    @InjectMocks
    private HarmonogramSerwisImpl service;

    private ZajeciaDTO dto;
    private Tor tor;
    private Instruktor instruktor;

    @BeforeEach
    void setUp() {
        tor = new Tor(1L, 1, 25, 1.8);
        instruktor = new Instruktor(2L, "Piotr", "Nowak", "piotr@example.com",
                "pass", "123", LocalDate.of(1990, 1, 1), "opis");

        dto = new ZajeciaDTO();
        dto.setDataGodzina(LocalDateTime.of(2026, Month.JULY, 6, 16, 0));
        dto.setCzasTrwaniaMin(45);
        dto.setTypZajec(TypZajec.DZIECI_GR_A);
        dto.setInstruktorId(2L);
        dto.setTorId(1L);
        dto.setMaxLiczbaMiejsc(10);
        dto.setCyklicznosc(Cyklicznosc.JEDNORAZOWE);
        dto.setDataKonca(null);
    }

    @Test
    void shouldAddSingleLessonSuccessfully() {
        when(torSerwis.sprawdzDostepnosc(eq(1L), any(), any())).thenReturn(true);
        when(torRepository.znajdzPoId(1L)).thenReturn(Optional.of(tor));
        when(uzytkownikRepository.znajdzPoId(2L)).thenReturn(Optional.of(instruktor));
        when(zajeciaRepository.znajdzKonfliktyInstruktora(any(), any(), any(), isNull())).thenReturn(List.of());
        when(zajeciaRepository.zapisz(any(Zajecia.class))).thenAnswer(inv -> {
            Zajecia z = inv.getArgument(0);
            z.setId(10L);
            return z;
        });

        List<Zajecia> result = service.dodajZajecia(dto);

        assertEquals(1, result.size());
        Zajecia z = result.get(0);
        assertEquals(10L, z.getId());
        assertEquals(TypZajec.DZIECI_GR_A, z.getTypZajec());
        assertEquals(tor, z.getTor());
        assertEquals(instruktor, z.getInstruktor());
        verify(powiadomienieSerwis).wyslij(any(), anyList(), anyString());
    }

    @Test
    void shouldThrowConflictWhenTorBusy() {
        when(torSerwis.sprawdzDostepnosc(eq(1L), any(), any())).thenReturn(false);
        when(torRepository.znajdzPoId(1L)).thenReturn(Optional.of(tor));
        // dodajemy brakujący mock dla instruktora
        when(uzytkownikRepository.znajdzPoId(2L)).thenReturn(Optional.of(instruktor));

        assertThrows(ConflictException.class, () -> service.dodajZajecia(dto));
    }

    @Test
    void shouldThrowConflictWhenInstructorBusy() {
        when(torSerwis.sprawdzDostepnosc(eq(1L), any(), any())).thenReturn(true);
        when(torRepository.znajdzPoId(1L)).thenReturn(Optional.of(tor));
        when(uzytkownikRepository.znajdzPoId(2L)).thenReturn(Optional.of(instruktor));
        when(zajeciaRepository.znajdzKonfliktyInstruktora(any(), any(), any(), isNull()))
                .thenReturn(List.of(mock(Zajecia.class)));

        assertThrows(ConflictException.class, () -> service.dodajZajecia(dto));
    }

    @Test
    void shouldEditLesson() {
        Zajecia zajecia = new Zajecia(5L, LocalDateTime.now(), 45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        zajecia.setTor(tor);
        zajecia.setInstruktor(instruktor);

        when(zajeciaRepository.znajdzPoId(5L)).thenReturn(Optional.of(zajecia));
        when(torRepository.znajdzPoId(1L)).thenReturn(Optional.of(tor));
        when(uzytkownikRepository.znajdzPoId(2L)).thenReturn(Optional.of(instruktor));
        when(zajeciaRepository.znajdzKonfliktyToru(any(), any(), any(), eq(5L))).thenReturn(List.of());
        when(zajeciaRepository.znajdzKonfliktyInstruktora(any(), any(), any(), eq(5L))).thenReturn(List.of());

        ZajeciaDTO editDto = new ZajeciaDTO();
        editDto.setDataGodzina(LocalDateTime.of(2026, Month.AUGUST, 1, 17, 0));
        editDto.setCzasTrwaniaMin(60);
        editDto.setTypZajec(TypZajec.DOROSLI_PODSTAWOWY);
        editDto.setInstruktorId(2L);
        editDto.setTorId(1L);
        editDto.setMaxLiczbaMiejsc(15);
        editDto.setCyklicznosc(Cyklicznosc.JEDNORAZOWE);

        Zajecia edited = service.edytujZajecia(5L, editDto);

        assertEquals(LocalDateTime.of(2026, Month.AUGUST, 1, 17, 0), edited.getDataGodzina());
        assertEquals(60, edited.getCzasTrwaniaMin());
        assertEquals(TypZajec.DOROSLI_PODSTAWOWY, edited.getTypZajec());
        assertEquals(15, edited.getMaxLiczbaMiejsc());
        verify(powiadomienieSerwis).powiadomOZmianieHarmonogramu(zajecia);
    }

    @Test
    void shouldDeleteLessonWithNotification() {
        Zajecia zajecia = new Zajecia(5L, LocalDateTime.now(), 45, TypZajec.DZIECI_GR_A, 10, Cyklicznosc.JEDNORAZOWE);
        when(zajeciaRepository.znajdzPoId(5L)).thenReturn(Optional.of(zajecia));

        service.usunZajecia(5L, false);

        verify(zajeciaRepository).usun(5L);
        verify(powiadomienieSerwis).wyslij(any(), anyList(), contains("odwołane"));
    }

    @Test
    void shouldGenerateCyclicLessons() {
        dto.setCyklicznosc(Cyklicznosc.CO_TYDZIEN);
        dto.setDataKonca(LocalDate.of(2026, Month.JULY, 27));

        when(torSerwis.sprawdzDostepnosc(any(), any(), any())).thenReturn(true);
        when(torRepository.znajdzPoId(1L)).thenReturn(Optional.of(tor));
        when(uzytkownikRepository.znajdzPoId(2L)).thenReturn(Optional.of(instruktor));
        when(zajeciaRepository.znajdzKonfliktyInstruktora(any(), any(), any(), isNull())).thenReturn(List.of());
        when(zajeciaRepository.zapisz(any())).thenAnswer(inv -> inv.getArgument(0));

        List<Zajecia> result = service.dodajZajecia(dto);

        assertEquals(4, result.size());
        assertEquals(LocalDate.of(2026, Month.JULY, 6), result.get(0).getDataGodzina().toLocalDate());
        assertEquals(LocalDate.of(2026, Month.JULY, 27), result.get(3).getDataGodzina().toLocalDate());
    }

    @Test
    void shouldThrowWhenDtoInvalid() {
        dto.setDataGodzina(null);
        assertThrows(ValidationException.class, () -> service.dodajZajecia(dto));
    }
}
