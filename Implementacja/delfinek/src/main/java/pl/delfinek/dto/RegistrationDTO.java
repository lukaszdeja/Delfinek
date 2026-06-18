package pl.delfinek.dto;

import java.time.LocalDate;

/**
 * Dane potrzebne do rejestracji nowego klienta
 */
public class RegistrationDTO {

    private String imie;
    private String nazwisko;
    private String email;
    private String haslo;
    private String nrTelefonu;
    private LocalDate dataUrodzenia;

    public RegistrationDTO() {
    }

    public RegistrationDTO(String imie, String nazwisko, String email, String haslo,
                           String nrTelefonu, LocalDate dataUrodzenia) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.email = email;
        this.haslo = haslo;
        this.nrTelefonu = nrTelefonu;
        this.dataUrodzenia = dataUrodzenia;
    }

    //gettery i settery

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getNrTelefonu() {
        return nrTelefonu;
    }

    public void setNrTelefonu(String nrTelefonu) {
        this.nrTelefonu = nrTelefonu;
    }

    public LocalDate getDataUrodzenia() {
        return dataUrodzenia;
    }

    public void setDataUrodzenia(LocalDate dataUrodzenia) {
        this.dataUrodzenia = dataUrodzenia;
    }
}
