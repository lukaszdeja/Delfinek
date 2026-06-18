package pl.delfinek.model;

import pl.delfinek.model.enums.StatusZapisu;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Zapis klienta na konkretne zajęcia.
 */
public class Zapis {

    private Long id;
    private LocalDateTime dataZapisu;
    private StatusZapisu status;

    private Klient klient;
    private Zajecia zajecia;

    public Zapis(Long id, Klient klient, Zajecia zajecia) {
        this.id = id;
        this.klient = klient;
        this.zajecia = zajecia;
        this.dataZapisu = LocalDateTime.now();
        this.status = StatusZapisu.AKTYWNY;
    }

    /**
     * Anuluje zapis - zmienia status i usuwa klienta z listy uczestników zajęć,
     * dzięki czemu getLiczbaMiejscWolnych() na zajęciach od razu odzwierciedla zwolnione miejsce.
     */
    public void anuluj() {
        if (this.status == StatusZapisu.ANULOWANY) {
            throw new IllegalStateException("Zapis jest już anulowany.");
        }
        this.status = StatusZapisu.ANULOWANY;
        if (zajecia != null && klient != null) {
            zajecia.usunKursanta(klient);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataZapisu() {
        return dataZapisu;
    }

    public StatusZapisu getStatus() {
        return status;
    }

    public void setStatus(StatusZapisu status) {
        this.status = status;
    }

    public Klient getKlient() {
        return klient;
    }

    public Zajecia getZajecia() {
        return zajecia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zapis that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Zapis{id=" + id + ", status=" + status + ", klient=" + klient + ", zajecia=" + zajecia + "}";
    }
}
