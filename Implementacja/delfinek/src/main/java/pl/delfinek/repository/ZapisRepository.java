package pl.delfinek.repository;

import pl.delfinek.model.Klient;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.Zapis;
import pl.delfinek.model.enums.StatusZapisu;

import java.util.List;

/**
 * Repozytorium zapisów na zajęcia.
 */
public class ZapisRepository extends InMemoryRepository<Zapis> {

    @Override
    protected Long pobierzId(Zapis encja) {
        return encja.getId();
    }

    @Override
    protected void ustawId(Zapis encja, Long id) {
        encja.setId(id);
    }

    public List<Zapis> znajdzAktywneZajeciaPoZajeciach(Zajecia zajecia) {
        return znajdzWszystkie().stream()
                .filter(z -> z.getZajecia() != null && z.getZajecia().equals(zajecia))
                .filter(z -> z.getStatus() == StatusZapisu.AKTYWNY)
                .toList();
    }

    public List<Zapis> znajdzPoKliencie(Klient klient) {
        return znajdzWszystkie().stream()
                .filter(z -> z.getKlient() != null && z.getKlient().equals(klient))
                .toList();
    }

    public List<Klient> znajdzAktywnychKursantowZajec(Zajecia zajecia) {
        return znajdzAktywneZajeciaPoZajeciach(zajecia).stream()
                .map(Zapis::getKlient)
                .distinct()
                .toList();
    }
}
