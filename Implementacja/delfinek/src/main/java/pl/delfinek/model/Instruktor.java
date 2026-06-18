package pl.delfinek.model;

import pl.delfinek.model.enums.Rola;

import java.time.LocalDate;

/**
 * Instruktor pływania - prowadzi zajęcia, ma wgląd w listę swoich kursantów.
 */
public class Instruktor extends Uzytkownik {

    private String opis;

    public Instruktor(Long id, String imie, String nazwisko, String email, String haslo,
                       String nrTelefonu, LocalDate dataUrodzenia, String opis) {
        super(id, imie, nazwisko, email, haslo, nrTelefonu, dataUrodzenia, Rola.INSTRUKTOR);
        this.opis = opis;
    }

    @Override
    public Rola getDomyslnaRola() {
        return Rola.INSTRUKTOR;
    }

    public void edytujSwojOpis(String nowyOpis) {
        this.opis = nowyOpis;
    }

    public String getOpis() {
        return opis;
    }
}
