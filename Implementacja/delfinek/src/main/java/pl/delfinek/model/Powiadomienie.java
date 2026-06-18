package pl.delfinek.model;

import pl.delfinek.model.enums.TypPowiadomienia;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Powiadomienie kierowane do konkretnego użytkownika (odbiorcy).
 */
public class Powiadomienie {

    private Long id;
    private String tytul;
    private String tresc;
    private TypPowiadomienia typ;
    private LocalDateTime dataCzas;
    private boolean przeczytane;
    private Uzytkownik odbiorca;

    public Powiadomienie(Long id, String tytul, String tresc, TypPowiadomienia typ, Uzytkownik odbiorca) {
        this.id = id;
        this.tytul = tytul;
        this.tresc = tresc;
        this.typ = typ;
        this.odbiorca = odbiorca;
        this.dataCzas = LocalDateTime.now();
        this.przeczytane = false;
    }

    public void oznaczJakoPrzeczytane() {
        this.przeczytane = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTytul() {
        return tytul;
    }

    public String getTresc() {
        return tresc;
    }

    public TypPowiadomienia getTyp() {
        return typ;
    }

    public LocalDateTime getDataCzas() {
        return dataCzas;
    }

    public boolean isPrzeczytane() {
        return przeczytane;
    }

    public Uzytkownik getOdbiorca() {
        return odbiorca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Powiadomienie that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Powiadomienie{id=" + id + ", typ=" + typ + ", przeczytane=" + przeczytane + "}";
    }
}
