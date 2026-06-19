package pl.delfinek.service.impl;

import pl.delfinek.exception.NotFoundException;
import pl.delfinek.model.Konwersacja;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.Wiadomosc;
import pl.delfinek.model.enums.TypPowiadomienia;
import pl.delfinek.repository.KonwersacjaRepository;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.service.ChatSerwis;
import pl.delfinek.service.PowiadomienieSerwis;

import java.util.List;

/**
 * Implementacja ChatSerwis.
 */
public class ChatSerwisImpl implements ChatSerwis {

    private final KonwersacjaRepository konwersacjaRepository;
    private final UzytkownikRepository uzytkownikRepository;
    private final PowiadomienieSerwis powiadomienieSerwis;

    public ChatSerwisImpl(KonwersacjaRepository konwersacjaRepository,
                          UzytkownikRepository uzytkownikRepository, PowiadomienieSerwis powiadomienieSerwis) {
        this.konwersacjaRepository = konwersacjaRepository;
        this.uzytkownikRepository = uzytkownikRepository;
        this.powiadomienieSerwis = powiadomienieSerwis;
    }

    @Override
    public Konwersacja pobierzLubUtworzKonwersacje(Long u1, Long u2) {
        return konwersacjaRepository.znajdzPomiedzyUzytkownikami(u1, u2)
                .orElseGet(() -> {
                    Uzytkownik uzytkownik1 = znajdzUzytkownikaAlboWyjatek(u1);
                    Uzytkownik uzytkownik2 = znajdzUzytkownikaAlboWyjatek(u2);
                    Konwersacja nowa = new Konwersacja(null, uzytkownik1, uzytkownik2);
                    return konwersacjaRepository.zapisz(nowa);
                });
    }
    /**
     * Ttworzy wiadomość w konwersacji i wysyła powiadomienie do drugiego uczestnika konwersacji.
     */
    @Override
    public Wiadomosc wyslijWiadomosc(Long konwId, Long nadawcaId, String tresc) {
        Konwersacja konwersacja = konwersacjaRepository.znajdzPoId(konwId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono konwersacji o id=" + konwId));
        Uzytkownik nadawca = znajdzUzytkownikaAlboWyjatek(nadawcaId);

        Wiadomosc wiadomosc = konwersacja.wyslijWiadomosc(nadawca, tresc, null);
        konwersacjaRepository.zapisz(konwersacja);

        Uzytkownik drugiUczestnik = ustalDrugiegoUczestnika(konwersacja, nadawca);
        if (drugiUczestnik != null) {
            powiadomienieSerwis.wyslij(
                    TypPowiadomienia.NOWA_WIADOMOSC,
                    List.of(drugiUczestnik),
                    "Nowa wiadomość od " + nadawca.getPelneImie());
        }

        return wiadomosc;
    }

    @Override
    public List<Wiadomosc> pobierzHistorie(Long konwId) {
        Konwersacja konwersacja = konwersacjaRepository.znajdzPoId(konwId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono konwersacji o id=" + konwId));
        return konwersacja.getWiadomosci();
    }

    private Uzytkownik ustalDrugiegoUczestnika(Konwersacja konwersacja, Uzytkownik nadawca) {
        if (konwersacja.getUczestnik1().equals(nadawca)) {
            return konwersacja.getUczestnik2();
        }
        if (konwersacja.getUczestnik2().equals(nadawca)) {
            return konwersacja.getUczestnik1();
        }
        return null;
    }

    private Uzytkownik znajdzUzytkownikaAlboWyjatek(Long id) {
        return uzytkownikRepository.znajdzPoId(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika o id=" + id));
    }
}
