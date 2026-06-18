package pl.delfinek.service;

import pl.delfinek.model.Klient;
import pl.delfinek.model.Zapis;

import java.util.List;

/**
 * Serwis odpowiedzialny za zapisy klientów na zajęcia oraz ich anulowanie.
 */
public interface ZapisSerwis {

    /**
     * Zapisuje klienta na zajęcia. Sprawdza dostępność miejsc (zajecia.isPelne()),
     * a po pomyślnym zapisie wysyła powiadomienie + e-mail z potwierdzeniem.
     *
     * @throws BusinessException jeśli brak wolnych miejsc
     */
    Zapis zapisz(Long idKlienta, Long idZajec);

    /**
     * Anuluje zapis. Jeśli cyklicznie=true, anuluje również wszystkie przyszłe
     * wystąpienia tego samego cyklu zajęć dla tego klienta.
     */
    void anuluj(Long idZapisu, boolean cyklicznie);

    List<Zapis> pobierzZapisyKlienta(Long idKlienta);

    List<Klient> pobierzKursantow(Long idZajec);
}
