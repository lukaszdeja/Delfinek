package pl.delfinek.repository;

import pl.delfinek.model.Tor;

import java.util.List;
import java.util.Optional;

public class TorRepository extends InMemoryRepository<Tor> {

    @Override
    protected Long pobierzId(Tor encja) {
        return encja.getId();
    }

    @Override
    protected void ustawId(Tor encja, Long id) {
        encja.setId(id);
    }

    public Optional<Tor> znajdzPoNumerze(int numer) {
        return znajdzWszystkie().stream()
                .filter(t -> t.getNumer() == numer)
                .findFirst();
    }

    public List<Tor> znajdzWszystkieWolne() {
        return znajdzWszystkie().stream()
                .filter(t -> t.getStatus() == pl.delfinek.model.enums.StatusToru.WOLNY)
                .toList();
    }
}
