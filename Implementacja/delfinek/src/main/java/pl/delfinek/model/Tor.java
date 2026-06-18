package pl.delfinek.model;

import pl.delfinek.model.enums.StatusToru;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Tor basenowy, na którym odbywają się zajęcia.
 */
public class Tor {

    private Long id;
    private int numer;
    private int dlugoscM;
    private double glebokoscM;
    private StatusToru status;
    private String uwagi;

    public Tor(Long id, int numer, int dlugoscM, double glebokoscM) {
        this.id = id;
        this.numer = numer;
        this.dlugoscM = dlugoscM;
        this.glebokoscM = glebokoscM;
        this.status = StatusToru.WOLNY;
    }

    /**
     * Sprawdza, czy tor jest wolny w danym przedziale czasowym, na podstawie
     * listy zajęć już przypisanych do tego toru. Status REMONT zawsze blokuje tor.
     *
     * Przyjmujemy listę
     * zajęć jako parametr.
     */
    public boolean isWolny(LocalDateTime od, LocalDateTime doCzasu, List<Zajecia> istniejaceZajeciaNaTymTorze) {
        if (status == StatusToru.REMONT) {
            return false;
        }
        if (istniejaceZajeciaNaTymTorze == null) {
            return true;
        }
        for (Zajecia z : istniejaceZajeciaNaTymTorze) {
            if (z.kolidujeCzasowo(od, doCzasu)) {
                return false;
            }
        }
        return true;
    }

    public void zglosRemont(LocalDate od, LocalDate doCzasu) {
        if (od == null || doCzasu == null || doCzasu.isBefore(od)) {
            throw new IllegalArgumentException("Nieprawidłowy zakres dat remontu.");
        }
        this.status = StatusToru.REMONT;
        this.uwagi = "Remont: " + od + " - " + doCzasu;
    }

    public void zakonczRemont() {
        this.status = StatusToru.WOLNY;
        this.uwagi = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumer() {
        return numer;
    }

    public int getDlugoscM() {
        return dlugoscM;
    }

    public double getGlebokoscM() {
        return glebokoscM;
    }

    public StatusToru getStatus() {
        return status;
    }

    public void setStatus(StatusToru status) {
        this.status = status;
    }

    public String getUwagi() {
        return uwagi;
    }

    public void setUwagi(String uwagi) {
        this.uwagi = uwagi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tor that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tor{id=" + id + ", numer=" + numer + ", status=" + status + "}";
    }
}
