package pl.delfinek.repository;

import pl.delfinek.model.Konwersacja;

import java.util.Optional;

/**
 * Repozytorium konwersacji.
 */
public class KonwersacjaRepository extends InMemoryRepository<Konwersacja> {

    @Override
    protected Long pobierzId(Konwersacja encja) {
        return encja.getId();
    }

    @Override
    protected void ustawId(Konwersacja encja, Long id) {
        encja.setId(id);
    }

    public Optional<Konwersacja> znajdzPomiedzyUzytkownikami(Long id1, Long id2) {
        return znajdzWszystkie().stream()
                .filter(k -> k.dotyczyUczestnikow(id1, id2)).findFirst();
    }
}
