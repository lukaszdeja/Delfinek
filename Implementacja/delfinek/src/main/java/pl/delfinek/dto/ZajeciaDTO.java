package pl.delfinek.dto;

import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.model.enums.TypZajec;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Obiekt przenoszenia danych używany przy dodawaniu/edycji zajęć
 * Rekord danych wejściowych z formularza administratora.
 */
public class ZajeciaDTO {

    private LocalDateTime dataGodzina;
    private int czasTrwaniaMin;
    private TypZajec typZajec;
    private Long instruktorId;
    private Long torId;
    private int maxLiczbaMiejsc;
    private String uwagi;
    private Cyklicznosc cyklicznosc;
    private LocalDate dataKonca;

    public ZajeciaDTO() {
    }

    public ZajeciaDTO(LocalDateTime dataGodzina, int czasTrwaniaMin, TypZajec typZajec,
                      Long instruktorId, Long torId, int maxLiczbaMiejsc, String uwagi,
                      Cyklicznosc cyklicznosc, LocalDate dataKonca) {
        this.dataGodzina = dataGodzina;
        this.czasTrwaniaMin = czasTrwaniaMin;
        this.typZajec = typZajec;
        this.instruktorId = instruktorId;
        this.torId = torId;
        this.maxLiczbaMiejsc = maxLiczbaMiejsc;
        this.uwagi = uwagi;
        this.cyklicznosc = cyklicznosc;
        this.dataKonca = dataKonca;
    }

    //gettetu i settery

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

    public Long getInstruktorId() {
        return instruktorId;
    }

    public void setInstruktorId(Long instruktorId) {
        this.instruktorId = instruktorId;
    }

    public Long getTorId() {
        return torId;
    }

    public void setTorId(Long torId) {
        this.torId = torId;
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
}
