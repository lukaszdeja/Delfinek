package pl.delfinek.service;

import pl.delfinek.model.Konwersacja;
import pl.delfinek.model.Wiadomosc;

import java.util.List;

/**
 * Serwis odpowiedzialny za czat między użytkownikami (np. instruktor <-> klient).
 */
public interface ChatSerwis {

    Konwersacja pobierzLubUtworzKonwersacje(Long u1, Long u2);

    Wiadomosc wyslijWiadomosc(Long konwId, Long nadawcaId, String tresc);

    List<Wiadomosc> pobierzHistorie(Long konwId);
}
