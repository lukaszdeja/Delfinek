package pl.delfinek.service;

import pl.delfinek.dto.ZajeciaDTO;
import pl.delfinek.model.Zajecia;

import java.time.LocalDate;
import java.util.List;

/**
 * Serwis odpowiedzialny za zarządzanie harmonogramem zajęć: dodawanie, edycję,
 * usuwanie, wyszukiwanie konfliktów oraz przydział instruktorów i torów.
 */
public interface HarmonogramSerwis {

    /**
     * Dodaje nowe zajęcia (lub serię zajęć cyklicznych) na podstawie DTO.
     * Wykonuje walidację, sprawdza konflikty toru/instruktora i wysyła powiadomienia.
     *
     * @throws pl.delfinek.exception.ValidationException jeśli dto jest niepoprawne
     * @throws pl.delfinek.exception.ConflictException    jeśli wykryto konflikt terminu
     */
    List<Zajecia> dodajZajecia(ZajeciaDTO dto);

    /**
     * Edytuje istniejące zajęcia, sprawdza konflikty (z wyłączeniem samych siebie)
     * i powiadamia instruktora oraz zapisanych kursantów o zmianie.
     */
    Zajecia edytujZajecia(Long id, ZajeciaDTO dto);

    /**
     * Usuwa zajęcia (opcjonalnie cyklicznie - wszystkie przyszłe wystąpienia)
     * i powiadamia instruktora oraz zapisanych kursantów o odwołaniu.
     */
    void usunZajecia(Long id, boolean cyklicznie);

    List<Zajecia> pobierzHarmonogram(LocalDate od, LocalDate doCzasu);

    /** Harmonogram konkretnego instruktora w zadanym przedziale dat. */
    List<Zajecia> pobierzHarmonogramInstruktora(Long instruktorId, LocalDate od, LocalDate doCzasu);

    boolean sprawdzKonflikt(ZajeciaDTO dto);

    void przydzielInstruktora(Long idZajec, Long idInstr);

    void przydzielTor(Long idZajec, Long idToru);

    Zajecia pobierzZajecia(Long zajeciaId);
}
