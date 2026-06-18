package pl.delfinek.service.impl;

import pl.delfinek.exception.BusinessException;
import pl.delfinek.exception.NotFoundException;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.Zapis;
import pl.delfinek.model.enums.TypPowiadomienia;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.repository.ZajeciaRepository;
import pl.delfinek.repository.ZapisRepository;
import pl.delfinek.service.PowiadomienieSerwis;
import pl.delfinek.service.ZapisSerwis;

import java.util.List;

/**
 * Implementacja ZapisSerwis.
 * Obsluguje zapis klienta na zajęcia i wysylanie powiadomien,
 * a takze anulowanie zapisu na zajeica przez klienta
 */
public class ZapisSerwisImpl implements ZapisSerwis {

    private final ZapisRepository zapisRepository;
    private final ZajeciaRepository zajeciaRepository;
    private final UzytkownikRepository uzytkownikRepository;
    private final PowiadomienieSerwis powiadomienieSerwis;

    public ZapisSerwisImpl(ZapisRepository zapisRepository, ZajeciaRepository zajeciaRepository,
                           UzytkownikRepository uzytkownikRepository, PowiadomienieSerwis powiadomienieSerwis) {
        this.zapisRepository = zapisRepository;
        this.zajeciaRepository = zajeciaRepository;
        this.uzytkownikRepository = uzytkownikRepository;
        this.powiadomienieSerwis = powiadomienieSerwis;
    }

    @Override
    public Zapis zapisz(Long idKlienta, Long idZajec) {
        Klient klient = znajdzKlientaAlboWyjatek(idKlienta);
        Zajecia zajecia = zajeciaRepository.znajdzPoId(idZajec)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono zajęć o id=" + idZajec));

        if (zajecia.isPelne()) {
            throw new BusinessException("Brak wolnych miejsc");
        }

        Zapis zapis = new Zapis(null, klient, zajecia);
        zajecia.dodajKursanta(klient);
        zapisRepository.zapisz(zapis);

        powiadomienieSerwis.powiadomONowymZapisie(zapis);

        // instruktor zajęć jest informowany o nowym kursancie.
        powiadomienieSerwis.powiadomONowymKursancie(zajecia, klient);

        return zapis;
    }

    @Override
    public void anuluj(Long idZapisu, boolean cyklicznie) {
        Zapis zapis = zapisRepository.znajdzPoId(idZapisu)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono zapisu o id=" + idZapisu));

        zapis.anuluj();
        zapisRepository.zapisz(zapis);

        powiadomienieSerwis.wyslij(
                TypPowiadomienia.ANULOWANIE_ZAPISU,
                List.of(zapis.getKlient()),
                "Anulowano zapis na zajęcia: " + zapis.getZajecia().getTypZajec()
                        + " w dniu " + zapis.getZajecia().getDataGodzina());

        if (cyklicznie) {
            anulujPrzyszleZapisyTegoCyklu(zapis);
        }
    }

    @Override
    public List<Zapis> pobierzZapisyKlienta(Long idKlienta) {
        Klient klient = znajdzKlientaAlboWyjatek(idKlienta);
        return zapisRepository.znajdzPoKliencie(klient);
    }

    @Override
    public List<Klient> pobierzKursantow(Long idZajec) {
        Zajecia zajecia = zajeciaRepository.znajdzPoId(idZajec)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono zajęć o id=" + idZajec));
        return zapisRepository.znajdzAktywnychKursantowZajec(zajecia);
    }

    private void anulujPrzyszleZapisyTegoCyklu(Zapis zrodlowy) {
        Zajecia zrodloweZajecia = zrodlowy.getZajecia();
        Klient klient = zrodlowy.getKlient();

        java.time.LocalDateTime teraz = java.time.LocalDateTime.now();
        List<Zapis> przyszleZapisy = zapisRepository.znajdzPoKliencie(klient).stream()
                .filter(z -> z.getStatus() == pl.delfinek.model.enums.StatusZapisu.AKTYWNY)
                .filter(z -> z.getZajecia().getTypZajec() == zrodloweZajecia.getTypZajec())
                .filter(z -> z.getZajecia().getCyklicznosc() == zrodloweZajecia.getCyklicznosc())
                .filter(z -> java.util.Objects.equals(z.getZajecia().getInstruktor(), zrodloweZajecia.getInstruktor()))
                .filter(z -> z.getZajecia().getDataGodzina().toLocalTime().equals(zrodloweZajecia.getDataGodzina().toLocalTime()))
                .filter(z -> z.getZajecia().getDataGodzina().isAfter(teraz))
                .toList();

        for (Zapis z : przyszleZapisy) {
            z.anuluj();
            zapisRepository.zapisz(z);
        }
    }

    private Klient znajdzKlientaAlboWyjatek(Long idKlienta) {
        Uzytkownik uzytkownik = uzytkownikRepository.znajdzPoId(idKlienta)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika o id=" + idKlienta));
        if (!(uzytkownik instanceof Klient klient)) {
            throw new BusinessException("Użytkownik o id=" + idKlienta + " nie jest klientem.");
        }
        return klient;
    }
}
