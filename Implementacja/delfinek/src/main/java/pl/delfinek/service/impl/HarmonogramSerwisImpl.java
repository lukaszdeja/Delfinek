package pl.delfinek.service.impl;

import pl.delfinek.dto.ZajeciaDTO;
import pl.delfinek.exception.ConflictException;
import pl.delfinek.exception.NotFoundException;
import pl.delfinek.exception.ValidationException;
import pl.delfinek.model.Instruktor;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Tor;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.enums.Cyklicznosc;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.repository.TorRepository;
import pl.delfinek.repository.ZajeciaRepository;
import pl.delfinek.repository.ZapisRepository;
import pl.delfinek.service.HarmonogramSerwis;
import pl.delfinek.service.PowiadomienieSerwis;
import pl.delfinek.service.TorSerwis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Harmonogram Serwis implementacja - obsługa funkcjonalności harmonogramu
 */
public class HarmonogramSerwisImpl implements HarmonogramSerwis {

    private final ZajeciaRepository zajeciaRepository;
    private final ZapisRepository zapisRepository;
    private final TorRepository torRepository;
    private final UzytkownikRepository uzytkownikRepository;
    private final TorSerwis torSerwis;
    private final PowiadomienieSerwis powiadomienieSerwis;

    public HarmonogramSerwisImpl(ZajeciaRepository zajeciaRepository, ZapisRepository zapisRepository,
                                 TorRepository torRepository, UzytkownikRepository uzytkownikRepository,
                                 TorSerwis torSerwis, PowiadomienieSerwis powiadomienieSerwis) {
        this.zajeciaRepository = zajeciaRepository;
        this.zapisRepository = zapisRepository;
        this.torRepository = torRepository;
        this.uzytkownikRepository = uzytkownikRepository;
        this.torSerwis = torSerwis;
        this.powiadomienieSerwis = powiadomienieSerwis;
    }

    @Override
    public List<Zajecia> dodajZajecia(ZajeciaDTO dto) {
        walidujDto(dto);

        Tor tor = dto.getTorId() != null ? znajdzTorAlboWyjatek(dto.getTorId()) : null;
        Instruktor instruktor = dto.getInstruktorId() != null ? znajdzInstruktoraAlboWyjatek(dto.getInstruktorId()) : null;

        if (tor != null) {
            boolean torWolny = torSerwis.sprawdzDostepnosc(
                    tor.getId(), dto.getDataGodzina(), dto.getDataGodzina().plusMinutes(dto.getCzasTrwaniaMin()));
            if (!torWolny) {
                throw new ConflictException("Konflikt: tor jest zajęty w wybranym terminie.");
            }
        }

        if (instruktor != null) {
            List<Zajecia> konfliktyInstruktora = zajeciaRepository.znajdzKonfliktyInstruktora(
                    instruktor, dto.getDataGodzina(), dto.getDataGodzina().plusMinutes(dto.getCzasTrwaniaMin()), null);
            if (!konfliktyInstruktora.isEmpty()) {
                throw new ConflictException("Konflikt: instruktor jest zajęty w wybranym terminie.");
            }
        }

        List<Zajecia> wygenerowane = generujTerminy(dto, tor, instruktor);
        for (Zajecia z : wygenerowane) {
            zajeciaRepository.zapisz(z);
        }

        if (instruktor != null && !wygenerowane.isEmpty()) {
            powiadomienieSerwis.wyslij(
                    pl.delfinek.model.enums.TypPowiadomienia.NOWE_ZAJECIA,
                    List.of(instruktor),
                    "Przydzielono Ci nowe zajęcia: " + dto.getTypZajec() + ", pierwszy termin: " + dto.getDataGodzina());
        }

        return wygenerowane;
    }

    @Override
    public Zajecia edytujZajecia(Long id, ZajeciaDTO dto) {
        Zajecia zajecia = pobierzZajecia(id);
        walidujDto(dto);

        Tor nowyTor = dto.getTorId() != null ? znajdzTorAlboWyjatek(dto.getTorId()) : null;
        Instruktor nowyInstruktor = dto.getInstruktorId() != null ? znajdzInstruktoraAlboWyjatek(dto.getInstruktorId()) : null;

        LocalDateTime nowyStart = dto.getDataGodzina();
        LocalDateTime nowyKoniec = nowyStart.plusMinutes(dto.getCzasTrwaniaMin());

        if (nowyTor != null) {
            List<Zajecia> konfliktyToru = zajeciaRepository.znajdzKonfliktyToru(nowyTor, nowyStart, nowyKoniec, zajecia.getId());
            if (!konfliktyToru.isEmpty()) {
                throw new ConflictException("Konflikt: tor jest zajęty w wybranym terminie.");
            }
        }
        if (nowyInstruktor != null) {
            List<Zajecia> konfliktyInstruktora = zajeciaRepository.znajdzKonfliktyInstruktora(nowyInstruktor, nowyStart, nowyKoniec, zajecia.getId());
            if (!konfliktyInstruktora.isEmpty()) {
                throw new ConflictException("Konflikt: instruktor jest zajęty w wybranym terminie.");
            }
        }

        zajecia.setDataGodzina(dto.getDataGodzina());
        zajecia.setCzasTrwaniaMin(dto.getCzasTrwaniaMin());
        zajecia.setTypZajec(dto.getTypZajec());
        zajecia.setMaxLiczbaMiejsc(dto.getMaxLiczbaMiejsc());
        zajecia.setUwagi(dto.getUwagi());
        zajecia.setCyklicznosc(dto.getCyklicznosc());
        zajecia.setDataKonca(dto.getDataKonca());
        zajecia.setTor(nowyTor);
        zajecia.setInstruktor(nowyInstruktor);
        zajeciaRepository.zapisz(zajecia);

        // przez repozytorium, by uniknąć cyklicznej zależności HarmonogramSerwis <-> ZapisSerwis
        List<Klient> kursanci = zapisRepository.znajdzAktywnychKursantowZajec(zajecia);
        kursanci.forEach(zajecia::dodajKursantaJesliNieobecny);

        powiadomienieSerwis.powiadomOZmianieHarmonogramu(zajecia);

        return zajecia;
    }

    @Override
    public void usunZajecia(Long id, boolean cyklicznie) {
        Zajecia zajecia = pobierzZajecia(id);

        List<Zajecia> doUsuniecia = new ArrayList<>();
        doUsuniecia.add(zajecia);
        if (cyklicznie && zajecia.getCyklicznosc() != Cyklicznosc.JEDNORAZOWE) {
            doUsuniecia.addAll(znajdzPrzyszleWystapieniaCyklu(zajecia));
        }

        List<Uzytkownik> odbiorcyPowiadomien = new ArrayList<>();
        for (Zajecia z : doUsuniecia) {
            if (z.getInstruktor() != null && !odbiorcyPowiadomien.contains(z.getInstruktor())) {
                odbiorcyPowiadomien.add(z.getInstruktor());
            }
            for (Klient k : zapisRepository.znajdzAktywnychKursantowZajec(z)) {
                if (!odbiorcyPowiadomien.contains(k)) {
                    odbiorcyPowiadomien.add(k);
                }
            }
        }

        for (Zajecia z : doUsuniecia) {
            zajeciaRepository.usun(z.getId());
        }

        powiadomienieSerwis.wyslij(
                pl.delfinek.model.enums.TypPowiadomienia.ANULOWANIE_ZAJEC,
                odbiorcyPowiadomien,
                "Zajęcia zostały odwołane: " + zajecia.getTypZajec() + " w dniu " + zajecia.getDataGodzina());
    }

    @Override
    public List<Zajecia> pobierzHarmonogram(LocalDate od, LocalDate doCzasu) {
        walidujZakresDat(od, doCzasu);
        return zajeciaRepository.znajdzWPrzedziale(od.atStartOfDay(), doCzasu.atTime(23, 59, 59));
    }

    @Override
    public List<Zajecia> pobierzHarmonogramInstruktora(Long instruktorId, LocalDate od, LocalDate doCzasu) {
        walidujZakresDat(od, doCzasu);
        return zajeciaRepository.znajdzPoInstruktorzeWPrzedziale(
                instruktorId, od.atStartOfDay(), doCzasu.atTime(23, 59, 59));
    }

    @Override
    public boolean sprawdzKonflikt(ZajeciaDTO dto) {
        walidujDto(dto);
        LocalDateTime start = dto.getDataGodzina();
        LocalDateTime koniec = start.plusMinutes(dto.getCzasTrwaniaMin());

        if (dto.getTorId() != null) {
            Tor tor = znajdzTorAlboWyjatek(dto.getTorId());
            if (!zajeciaRepository.znajdzKonfliktyToru(tor, start, koniec, null).isEmpty()) {
                return true;
            }
        }
        if (dto.getInstruktorId() != null) {
            Instruktor instruktor = znajdzInstruktoraAlboWyjatek(dto.getInstruktorId());
            if (!zajeciaRepository.znajdzKonfliktyInstruktora(instruktor, start, koniec, null).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void przydzielInstruktora(Long idZajec, Long idInstr) {
        Zajecia zajecia = pobierzZajecia(idZajec);
        Instruktor instruktor = znajdzInstruktoraAlboWyjatek(idInstr);

        List<Zajecia> konflikty = zajeciaRepository.znajdzKonfliktyInstruktora(
                instruktor, zajecia.getDataGodzina(), zajecia.getDataZakonczenia(), zajecia.getId());
        if (!konflikty.isEmpty()) {
            throw new ConflictException("Konflikt: instruktor jest zajęty w wybranym terminie.");
        }

        zajecia.setInstruktor(instruktor);
        zajeciaRepository.zapisz(zajecia);
    }

    @Override
    public void przydzielTor(Long idZajec, Long idToru) {
        Zajecia zajecia = pobierzZajecia(idZajec);
        Tor tor = znajdzTorAlboWyjatek(idToru);

        List<Zajecia> konflikty = zajeciaRepository.znajdzKonfliktyToru(
                tor, zajecia.getDataGodzina(), zajecia.getDataZakonczenia(), zajecia.getId());
        if (!konflikty.isEmpty()) {
            throw new ConflictException("Konflikt: tor jest zajęty w wybranym terminie.");
        }

        zajecia.setTor(tor);
        zajeciaRepository.zapisz(zajecia);
    }

    @Override
    public Zajecia pobierzZajecia(Long zajeciaId) {
        return zajeciaRepository.znajdzPoId(zajeciaId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono zajęć o id=" + zajeciaId));
    }

    // metody pomocnicze

    /**
     * Generuje listę instancji Zajecia na podstawie cykliczności DTO:
     * JEDNORAZOWE -> jedna instancja, CO_TYDZIEN/CO_DWA_TYGODNIE -> seria
     * instancji od dataGodzina do dataKonca (włącznie) z odpowiednim krokiem.
     */
    private List<Zajecia> generujTerminy(ZajeciaDTO dto, Tor tor, Instruktor instruktor) {
        List<Zajecia> wynik = new ArrayList<>();
        Cyklicznosc cyklicznosc = dto.getCyklicznosc() == null ? Cyklicznosc.JEDNORAZOWE : dto.getCyklicznosc();

        if (cyklicznosc == Cyklicznosc.JEDNORAZOWE || dto.getDataKonca() == null) {
            wynik.add(zbudujZajecia(dto, dto.getDataGodzina(), tor, instruktor, null));
            return wynik;
        }

        String cyklId = java.util.UUID.randomUUID().toString();
        int krokDni = cyklicznosc == Cyklicznosc.CO_DWA_TYGODNIE ? 14 : 7;
        LocalDateTime aktualnyTermin = dto.getDataGodzina();
        LocalDate dataKonca = dto.getDataKonca();

        while (!aktualnyTermin.toLocalDate().isAfter(dataKonca)) {
            wynik.add(zbudujZajecia(dto, aktualnyTermin, tor, instruktor, cyklId));
            aktualnyTermin = aktualnyTermin.plusDays(krokDni);
        }
        return wynik;
    }

    private Zajecia zbudujZajecia(ZajeciaDTO dto, LocalDateTime termin, Tor tor, Instruktor instruktor, String cyklId) {
        Zajecia zajecia = new Zajecia(
                null, termin, dto.getCzasTrwaniaMin(), dto.getTypZajec(),
                dto.getMaxLiczbaMiejsc(), dto.getCyklicznosc());
        zajecia.setUwagi(dto.getUwagi());
        zajecia.setDataKonca(dto.getDataKonca());
        zajecia.setTor(tor);
        zajecia.setInstruktor(instruktor);
        zajecia.setCyklId(cyklId);
        return zajecia;
    }

    /**
     * Znajduje przyszłe (względem teraz) wystąpienia tej samej serii cyklicznej -
     * identyfikowane jednoznacznie przez cyklId, niezależnie od tego, czy dany
     * termin został później przesunięty godzinowo przez edycję. Dla zajęć bez
     * cyklId (JEDNORAZOWE) zwraca listę pustą - nie należą do żadnej serii.
     */
    private List<Zajecia> znajdzPrzyszleWystapieniaCyklu(Zajecia zrodlowe) {
        if (zrodlowe.getCyklId() == null) {
            return List.of();
        }
        LocalDateTime teraz = LocalDateTime.now();
        return zajeciaRepository.znajdzWszystkie().stream()
                .filter(z -> !z.getId().equals(zrodlowe.getId()))
                .filter(z -> zrodlowe.getCyklId().equals(z.getCyklId()))
                .filter(z -> z.getDataGodzina().isAfter(teraz))
                .toList();
    }

    private void walidujDto(ZajeciaDTO dto) {
        List<String> bledy = new ArrayList<>();
        if (dto == null) {
            throw new ValidationException("ZajeciaDTO nie może być null.");
        }
        if (dto.getDataGodzina() == null) {
            bledy.add("Data i godzina są wymagane.");
        }
        if (dto.getCzasTrwaniaMin() <= 0) {
            bledy.add("Czas trwania musi być większy od zera.");
        }
        if (dto.getTypZajec() == null) {
            bledy.add("Typ zajęć jest wymagany.");
        }
        if (dto.getMaxLiczbaMiejsc() <= 0) {
            bledy.add("Maksymalna liczba miejsc musi być większa od zera.");
        }
        if (dto.getCyklicznosc() != null && dto.getCyklicznosc() != Cyklicznosc.JEDNORAZOWE && dto.getDataKonca() == null) {
            bledy.add("Dla zajęć cyklicznych wymagana jest data zakończenia cyklu.");
        }
        if (!bledy.isEmpty()) {
            throw new ValidationException(bledy);
        }
    }

    private void walidujZakresDat(LocalDate od, LocalDate doCzasu) {
        if (od == null || doCzasu == null || doCzasu.isBefore(od)) {
            throw new ValidationException("Nieprawidłowy zakres dat.");
        }
    }

    private Tor znajdzTorAlboWyjatek(Long torId) {
        return torRepository.znajdzPoId(torId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono toru o id=" + torId));
    }

    private Instruktor znajdzInstruktoraAlboWyjatek(Long instruktorId) {
        Uzytkownik uzytkownik = uzytkownikRepository.znajdzPoId(instruktorId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika o id=" + instruktorId));
        if (!(uzytkownik instanceof Instruktor instruktor)) {
            throw new ValidationException("Użytkownik o id=" + instruktorId + " nie jest instruktorem.");
        }
        return instruktor;
    }
}
