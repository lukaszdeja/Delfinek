package pl.delfinek.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generyczne repozytorium odpowiadające za imitację połączenia kodu z bazą danych
 * Baza danych nie jest implementowana, repozytoria, imitują implementację w pamięci RAM
 * @param <T>  typ encji
 * @param <ID> typ identyfikatora encji
 */
public interface Repository<T, ID> {

    T zapisz(T encja);

    Optional<T> znajdzPoId(ID id);

    List<T> znajdzWszystkie();

    void usun(ID id);

    boolean istnieje(ID id);
}
