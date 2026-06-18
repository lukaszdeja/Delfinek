package pl.delfinek.model;

import pl.delfinek.model.enums.Rola;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstrakcyjna klasa bazowa dla wszystkich użytkowników systemu.
 * Konkretne role (Klient, Instruktor, Administrator) dziedziczą po tej klasie.
 */
public abstract class Uzytkownik {

    private Long id;
    private String imie;
    private String nazwisko;
    private String email;
    private String haslo;
    private String nrTelefonu;
    private LocalDate dataUrodzenia;
    private byte[] zdjecie;
    private LocalDateTime dataRejestracji;
    private Rola rola;

    private final List<Powiadomienie> powiadomienia = new ArrayList<>();

    protected Uzytkownik(Long id, String imie, String nazwisko, String email, String haslo,
                          String nrTelefonu, LocalDate dataUrodzenia, Rola rola) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.email = email;
        this.haslo = haslo;
        this.nrTelefonu = nrTelefonu;
        this.dataUrodzenia = dataUrodzenia;
        this.rola = rola;
        this.dataRejestracji = LocalDateTime.now();
    }

    /**
     * Zwracanie roli.
     */
    public abstract Rola getDomyslnaRola();

    /**
     * Prosta weryfikacja danych logowania - porównanie e-maila i hasła.
     */
    public boolean zalogujSie(String email, String haslo) {
        return Objects.equals(this.email, email) && Objects.equals(this.haslo, haslo);
    }

    /**
     * Symboliczne wylogowanie - w uproszczonej wersji nie ma tu nic do zrobienia
     * poza miejscem na ewentualne czyszczenie stanu sesji/tokena po stronie wywołującego.
     */
    public void wylogujSie() {
        
    }

    public void zmienHaslo(String stare, String nowe) {
        if (!Objects.equals(this.haslo, stare)) {
            throw new IllegalArgumentException("Stare hasło jest nieprawidłowe.");
        }
        if (nowe == null || nowe.isBlank()) {
            throw new IllegalArgumentException("Nowe hasło nie może być puste.");
        }
        this.haslo = nowe;
    }

    /**
     * Reset hasła na poziomie encji - ustawia nowe hasło bezpośrednio.
     * Logika będzie w serwisach.
     */
    public void resetujHaslo(String noweHaslo) {
        if (noweHaslo == null || noweHaslo.isBlank()) {
            throw new IllegalArgumentException("Nowe hasło nie może być puste.");
        }
        this.haslo = noweHaslo;
    }

    public void edytujProfil(String imie, String nazwisko, String nrTelefonu, LocalDate dataUrodzenia) {
        if (imie != null && !imie.isBlank()) {
            this.imie = imie;
        }
        if (nazwisko != null && !nazwisko.isBlank()) {
            this.nazwisko = nazwisko;
        }
        if (nrTelefonu != null && !nrTelefonu.isBlank()) {
            this.nrTelefonu = nrTelefonu;
        }
        if (dataUrodzenia != null) {
            this.dataUrodzenia = dataUrodzenia;
        }
    }

    public List<Powiadomienie> pobierzPowiadomienia() {
        return List.copyOf(powiadomienia);
    }

    public void dodajPowiadomienie(Powiadomienie powiadomienie) {
        this.powiadomienia.add(powiadomienie);
    }

    // gettery i settery

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImie() {
        return imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    protected String getHaslo() {
        return haslo;
    }

    public String getNrTelefonu() {
        return nrTelefonu;
    }

    public LocalDate getDataUrodzenia() {
        return dataUrodzenia;
    }

    public byte[] getZdjecie() {
        return zdjecie;
    }

    public void setZdjecie(byte[] zdjecie) {
        this.zdjecie = zdjecie;
    }

    public LocalDateTime getDataRejestracji() {
        return dataRejestracji;
    }

    public Rola getRola() {
        return rola;
    }

    public void setRola(Rola rola) {
        this.rola = rola;
    }

    public String getPelneImie() {
        return imie + " " + nazwisko;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Uzytkownik that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", email='" + email + "', rola=" + rola + "}";
    }
}
