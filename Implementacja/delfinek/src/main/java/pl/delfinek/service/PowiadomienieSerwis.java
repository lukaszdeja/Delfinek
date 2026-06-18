package pl.delfinek.service;

import pl.delfinek.model.Klient;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.Zapis;
import pl.delfinek.model.enums.TypPowiadomienia;

import java.util.List;

/**
 * Serwis odpowiedzialny za generowanie i wysyłkę powiadomień w systemie
 * (w aplikacji + powiązany e-mail dla niektórych typów zdarzeń).
 */
public interface PowiadomienieSerwis {

    void wyslij(TypPowiadomienia typ, List<Uzytkownik> odbiorcy, String tresc);

    void powiadomOZmianieHarmonogramu(Zajecia zajecia);

    void powiadomONowymZapisie(Zapis zapis);

    void powiadomONowymKursancie(Zajecia zajecia, Klient klient);

    /** Wysyła ogólny komunikat administratora do wybranej grupy odbiorców (rola); zwraca liczbę wysłanych powiadomień. */
    int wyslijKomunikat(String tresc, List<pl.delfinek.model.enums.Rola> grupaOdbiorcow);
}
