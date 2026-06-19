package pl.delfinek.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model konwersacji pomiędzy dwoma użytkownikami na przykład klientem a instruktorem
 */
public class Konwersacja {

    private Long id;
    private final LocalDateTime dataUtworzenia;

    private Uzytkownik uczestnik1;
    private Uzytkownik uczestnik2;

    private final List<Wiadomosc> wiadomosci = new ArrayList<>();

    public Konwersacja(Long id, Uzytkownik uczestnik1, Uzytkownik uczestnik2) {
        this.id = id;
        this.uczestnik1 = uczestnik1;
        this.uczestnik2 = uczestnik2;
        this.dataUtworzenia = LocalDateTime.now();
    }

    public List<Wiadomosc> getWiadomosci() {
        return List.copyOf(wiadomosci);
    }

    /**
     * Tworzy i dodaje nową wiadomość do konwersacji. Walidacja, czy nadawca
     * jest jednym z uczestników konwersacji, należy do warstwy serwisowej
     */
    public Wiadomosc wyslijWiadomosc(Uzytkownik nadawca, String tresc, Long nowyIdWiadomosci) {
        if (tresc == null || tresc.isBlank()) {
            throw new IllegalArgumentException("Treść wiadomości nie może być pusta.");
        }
        Wiadomosc wiadomosc = new Wiadomosc(nowyIdWiadomosci, tresc, nadawca);
        wiadomosci.add(wiadomosc);
        return wiadomosc;
    }

    /** Sprawdza, czy podana para użytkowników (w dowolnej kolejności) odpowiada tej konwersacji. */
    public boolean dotyczyUczestnikow(Long id1, Long id2) {
        if (uczestnik1 == null || uczestnik2 == null) {
            return false;
        }
        boolean wariant1 = Objects.equals(uczestnik1.getId(), id1) && Objects.equals(uczestnik2.getId(), id2);
        boolean wariant2 = Objects.equals(uczestnik1.getId(), id2) && Objects.equals(uczestnik2.getId(), id1);
        return wariant1 || wariant2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataUtworzenia() {
        return dataUtworzenia;
    }

    public Uzytkownik getUczestnik1() {
        return uczestnik1;
    }

    public Uzytkownik getUczestnik2() {
        return uczestnik2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Konwersacja that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Konwersacja{id=" + id + ", uczestnik1=" + uczestnik1 + ", uczestnik2=" + uczestnik2 + "}";
    }
}
