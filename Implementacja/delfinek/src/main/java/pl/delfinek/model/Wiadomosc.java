package pl.delfinek.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Pojedyncza wiadomość w ramach konwersacji (czatu) między dwoma użytkownikami.
 */
public class Wiadomosc {

    private Long id;
    private String tresc;
    private LocalDateTime dataCzas;
    private boolean przeczytana;
    private Uzytkownik nadawca;

    public Wiadomosc(Long id, String tresc, Uzytkownik nadawca) {
        this.id = id;
        this.tresc = tresc;
        this.nadawca = nadawca;
        this.dataCzas = LocalDateTime.now();
        this.przeczytana = false;
    }

    public void oznaczJakoPrzeczytana() {
        this.przeczytana = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTresc() {
        return tresc;
    }

    public LocalDateTime getDataCzas() {
        return dataCzas;
    }

    public boolean isPrzeczytana() {
        return przeczytana;
    }

    public Uzytkownik getNadawca() {
        return nadawca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wiadomosc that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Wiadomosc{id=" + id + ", nadawca=" + nadawca + ", przeczytana=" + przeczytana + "}";
    }
}
