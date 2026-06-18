package pl.delfinek.repository;

import pl.delfinek.model.Instruktor;
import pl.delfinek.model.Tor;
import pl.delfinek.model.Zajecia;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repozytorium zajęć. Metody odpowiadają zapytaniom hipotetycznym zapytaniom SQL
 * ktore w rzeczywistosci nie są implementowane
 */
public class ZajeciaRepository extends InMemoryRepository<Zajecia> {

    @Override
    protected Long pobierzId(Zajecia encja) {
        return encja.getId();
    }

    @Override
    protected void ustawId(Zajecia encja, Long id) {
        encja.setId(id);
    }

    public List<Zajecia> znajdzPoInstruktorzeWPrzedziale(Long instruktorId, LocalDateTime od, LocalDateTime doCzasu) {
        return znajdzWszystkie().stream()
                .filter(z -> z.getInstruktor() != null && z.getInstruktor().getId().equals(instruktorId))
                .filter(z -> !z.getDataGodzina().isAfter(doCzasu) && !z.getDataZakonczenia().isBefore(od))
                .toList();
    }

    public List<Zajecia> znajdzWPrzedziale(LocalDateTime od, LocalDateTime doCzasu) {
        return znajdzWszystkie().stream()
                .filter(z -> !z.getDataGodzina().isAfter(doCzasu) && !z.getDataZakonczenia().isBefore(od))
                .toList();
    }

    /** Konflikty terminu dla danego toru (z wyłączeniem opcjonalnie samych zajęć poddawanych edycji). */
    public List<Zajecia> znajdzKonfliktyToru(Tor tor, LocalDateTime od, LocalDateTime doCzasu, Long wyklczIdZajec) {
        return znajdzWszystkie().stream()
                .filter(z -> z.getTor() != null && z.getTor().equals(tor))
                .filter(z -> wyklczIdZajec == null || !z.getId().equals(wyklczIdZajec))
                .filter(z -> z.kolidujeCzasowo(od, doCzasu))
                .toList();
    }

    /** Konflikty terminu dla danego instruktora (z wyłączeniem opcjonalnie samych zajęć poddawanych edycji). */
    public List<Zajecia> znajdzKonfliktyInstruktora(Instruktor instruktor, LocalDateTime od, LocalDateTime doCzasu, Long wyklczIdZajec) {
        return znajdzWszystkie().stream()
                .filter(z -> z.getInstruktor() != null && z.getInstruktor().equals(instruktor))
                .filter(z -> wyklczIdZajec == null || !z.getId().equals(wyklczIdZajec))
                .filter(z -> z.kolidujeCzasowo(od, doCzasu))
                .toList();
    }

    public List<Zajecia> znajdzPoTorze(Tor tor) {
        return znajdzWszystkie().stream()
                .filter(z -> z.getTor() != null && z.getTor().equals(tor))
                .toList();
    }
}
