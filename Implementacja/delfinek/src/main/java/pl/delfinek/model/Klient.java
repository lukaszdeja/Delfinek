package pl.delfinek.model;

import pl.delfinek.model.enums.PoziomPlywania;
import pl.delfinek.model.enums.Rola;

import java.time.LocalDate;

/**
 * Klient szkoły pływania - zapisuje się na zajęcia, przegląda harmonogram.
 */
public class Klient extends Uzytkownik {

    private PoziomPlywania poziomPlywania;
    private String notatkaInstruktora;

    public Klient(Long id, String imie, String nazwisko, String email, String haslo,
                  String nrTelefonu, LocalDate dataUrodzenia) {
        super(id, imie, nazwisko, email, haslo, nrTelefonu, dataUrodzenia, Rola.KLIENT);
        this.poziomPlywania = PoziomPlywania.POCZATKUJACY;
    }

    @Override
    public Rola getDomyslnaRola() {
        return Rola.KLIENT;
    }


    public void zapiszSieNaZajecia(Zajecia zajecia) {
        if (zajecia == null) {
            throw new IllegalArgumentException("Zajęcia nie mogą być null.");
        }
        zajecia.dodajKursanta(this);
    }

    public void anulujZapis(Zapis zapis) {
        if (zapis == null) {
            throw new IllegalArgumentException("Zapis nie może być null.");
        }
        zapis.anuluj();
    }

    public PoziomPlywania getPoziomPlywania() {
        return poziomPlywania;
    }

    public void setPoziomPlywania(PoziomPlywania poziomPlywania) {
        this.poziomPlywania = poziomPlywania;
    }

    public String getNotatkaInstruktora() {
        return notatkaInstruktora;
    }

    public void setNotatkaInstruktora(String notatkaInstruktora) {
        this.notatkaInstruktora = notatkaInstruktora;
    }
}
