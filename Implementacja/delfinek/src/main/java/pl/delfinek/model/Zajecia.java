package pl.delfinek.model;

import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.TypZajec;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Zajęcia pływania - konkretny termin w harmonogramie.
 *
 * Mogą istnieć bez przypisanego instuktora, tymczasowo gdy np. planujemy zajęcia w przód i nie wiemy który instruktor
 * będzie je prowadził.
 *
 * Liczba wolnych miejsc jest liczona dynamicznie na podstawie aktywnych zapisów
 */
public class Zajecia {

    private Long id;
    private LocalDateTime dataGodzina;
    private int czasTrwaniaMin;
    private TypZajec typZajec;
    private int maxLiczbaMiejsc;
    private String uwagi;
    private Cyklicznosc cyklicznosc;
    private LocalDate dataKonca;

    private Instruktor instruktor;
    private Tor tor;

    /** Aktywni i historyczni kursanci powiązani przez zapisy aktywne (widok pomocniczy). */
    private final List<Klient> uczestnicy = new ArrayList<>();

    public Zajecia(Long id, LocalDateTime dataGodzina, int czasTrwaniaMin, TypZajec typZajec,
                    int maxLiczbaMiejsc, Cyklicznosc cyklicznosc) {
        this.id = id;
        this.dataGodzina = dataGodzina;
        this.czasTrwaniaMin = czasTrwaniaMin;
        this.typZajec = typZajec;
        this.maxLiczbaMiejsc = maxLiczbaMiejsc;
        this.cyklicznosc = cyklicznosc == null ? Cyklicznosc.JEDNORAZOWE : cyklicznosc;
    }

    public LocalDateTime getDataZakonczenia() {
        return dataGodzina.plusMinutes(czasTrwaniaMin);
    }

    /**
     * Sprawdza, czy te zajęcia kolidują czasowo z podanym przedziałem [od, do).
     * Wykorzystywane przez Tor.isWolny oraz przez HarmonogramSerwis.sprawdzKonflikt
     * (sprawdzanie konfliktu toru/instruktora przy dodawaniu/edycji zajęć).
     */
    public boolean kolidujeCzasowo(LocalDateTime od, LocalDateTime doCzasu) {
        LocalDateTime tenStart = this.dataGodzina;
        LocalDateTime tenKoniec = getDataZakonczenia();
        return tenStart.isBefore(doCzasu) && od.isBefore(tenKoniec);
    }

    public int getLiczbaMiejscWolnych() {
        return Math.max(0, maxLiczbaMiejsc - uczestnicy.size());
    }

    public boolean isPelne() {
        return getLiczbaMiejscWolnych() <= 0;
    }

    /**
     * Dodaje kursanta do listy uczestników zajęć. Rzuca wyjątek, jeśli brak miejsc
     * lub klient jest już zapisany.
     */
    public void dodajKursanta(Klient klient) {
        if (klient == null) {
            throw new IllegalArgumentException("Klient nie może być null.");
        }
        if (isPelne()) {
            throw new IllegalStateException("Brak wolnych miejsc na zajęciach.");
        }
        if (uczestnicy.contains(klient)) {
            throw new IllegalStateException("Klient jest już zapisany na te zajęcia.");
        }
        uczestnicy.add(klient);
    }

    public void usunKursanta(Klient klient) {
        uczestnicy.remove(klient);
    }

    /**
     * Dodaje kursanta na zajęcia - nie rzuca wyjątku, jeśli
     * klient jest już na liście; pomija też sprawdzanie limitu miejsc,
     * to będzie sprawdzane w ZapisSerwis.
     */
    public void dodajKursantaJesliNieobecny(Klient klient) {
        if (klient != null && !uczestnicy.contains(klient)) {
            uczestnicy.add(klient);
        }
    }
    
    //gettery i settery

    public List<Klient> getListaUczestnikow() {
        return List.copyOf(uczestnicy);
    }

    // gettery i settery

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataGodzina() {
        return dataGodzina;
    }

    public void setDataGodzina(LocalDateTime dataGodzina) {
        this.dataGodzina = dataGodzina;
    }

    public int getCzasTrwaniaMin() {
        return czasTrwaniaMin;
    }

    public void setCzasTrwaniaMin(int czasTrwaniaMin) {
        this.czasTrwaniaMin = czasTrwaniaMin;
    }

    public TypZajec getTypZajec() {
        return typZajec;
    }

    public void setTypZajec(TypZajec typZajec) {
        this.typZajec = typZajec;
    }

    public int getMaxLiczbaMiejsc() {
        return maxLiczbaMiejsc;
    }

    public void setMaxLiczbaMiejsc(int maxLiczbaMiejsc) {
        this.maxLiczbaMiejsc = maxLiczbaMiejsc;
    }

    public String getUwagi() {
        return uwagi;
    }

    public void setUwagi(String uwagi) {
        this.uwagi = uwagi;
    }

    public Cyklicznosc getCyklicznosc() {
        return cyklicznosc;
    }

    public void setCyklicznosc(Cyklicznosc cyklicznosc) {
        this.cyklicznosc = cyklicznosc;
    }

    public LocalDate getDataKonca() {
        return dataKonca;
    }

    public void setDataKonca(LocalDate dataKonca) {
        this.dataKonca = dataKonca;
    }

    public Instruktor getInstruktor() {
        return instruktor;
    }

    public void setInstruktor(Instruktor instruktor) {
        this.instruktor = instruktor;
    }

    public Tor getTor() {
        return tor;
    }

    public void setTor(Tor tor) {
        this.tor = tor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zajecia that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Zajecia{id=" + id + ", typZajec=" + typZajec + ", dataGodzina=" + dataGodzina + "}";
    }
}
