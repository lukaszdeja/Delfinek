package pl.delfinek.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Wspólna implementacja repozytorium "w pamięci" (in-memory), symulująca
 * trwałość danych bez realnej bazy. Każde konkretne repozytorium rozszerza tę klasę
 *
 * @param <T> typ encji, której Long id jest kluczem
 */
public abstract class InMemoryRepository<T> implements Repository<T, Long> {

    protected final Map<Long, T> przechowalnia = new ConcurrentHashMap<>();
    protected final AtomicLong sekwencjaId = new AtomicLong(0);

    protected abstract Long pobierzId(T encja);

    protected abstract void ustawId(T encja, Long id);

    @Override
    public T zapisz(T encja) {
        Long id = pobierzId(encja);
        if (id == null) {
            id = sekwencjaId.incrementAndGet();
            ustawId(encja, id);
        }
        przechowalnia.put(id, encja);
        return encja;
    }

    @Override
    public Optional<T> znajdzPoId(Long id) {
        return Optional.ofNullable(przechowalnia.get(id));
    }

    @Override
    public List<T> znajdzWszystkie() {
        return new ArrayList<>(przechowalnia.values());
    }

    @Override
    public void usun(Long id) {
        przechowalnia.remove(id);
    }

    @Override
    public boolean istnieje(Long id) {
        return przechowalnia.containsKey(id);
    }
}
