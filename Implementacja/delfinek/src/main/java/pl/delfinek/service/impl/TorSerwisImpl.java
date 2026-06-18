package pl.delfinek.service.impl;

import pl.delfinek.exception.NotFoundException;
import pl.delfinek.model.Tor;
import pl.delfinek.model.Zajecia;
import pl.delfinek.repository.TorRepository;
import pl.delfinek.repository.ZajeciaRepository;
import pl.delfinek.service.TorSerwis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementuje TorSerwis. Sprawdzanie dostępności toru.
 */
public class TorSerwisImpl implements TorSerwis {

    private final TorRepository torRepository;
    private final ZajeciaRepository zajeciaRepository;

    public TorSerwisImpl(TorRepository torRepository, ZajeciaRepository zajeciaRepository) {
        this.torRepository = torRepository;
        this.zajeciaRepository = zajeciaRepository;
    }

    @Override
    public List<Tor> pobierzTory() {
        return torRepository.znajdzWszystkie();
    }

    @Override
    public boolean sprawdzDostepnosc(Long idToru, LocalDateTime od, LocalDateTime doCzasu) {
        Tor tor = znajdzTorAlboWyjatek(idToru);
        List<Zajecia> istniejaceZajecia = zajeciaRepository.znajdzPoTorze(tor);
        return tor.isWolny(od, doCzasu, istniejaceZajecia);
    }

    @Override
    public void zglosRemont(Long idToru, LocalDate od, LocalDate doCzasu) {
        Tor tor = znajdzTorAlboWyjatek(idToru);
        tor.zglosRemont(od, doCzasu);
        torRepository.zapisz(tor);
    }

    @Override
    public void zakonczRemont(Long idToru) {
        Tor tor = znajdzTorAlboWyjatek(idToru);
        tor.zakonczRemont();
        torRepository.zapisz(tor);
    }

    private Tor znajdzTorAlboWyjatek(Long idToru) {
        return torRepository.znajdzPoId(idToru)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono toru o id=" + idToru));
    }
}
