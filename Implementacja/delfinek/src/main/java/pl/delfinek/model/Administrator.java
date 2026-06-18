package pl.delfinek.model;

import pl.delfinek.model.enums.Rola;

import java.time.LocalDate;

/**
 * Administrator systemu.
 */
public class Administrator extends Uzytkownik {

    public Administrator(Long id, String imie, String nazwisko, String email, String haslo,
                          String nrTelefonu, LocalDate dataUrodzenia) {
        super(id, imie, nazwisko, email, haslo, nrTelefonu, dataUrodzenia, Rola.ADMINISTRATOR);
    }

    @Override
    public Rola getDomyslnaRola() {
        return Rola.ADMINISTRATOR;
    }
}
