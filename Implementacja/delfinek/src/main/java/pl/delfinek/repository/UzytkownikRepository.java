package pl.delfinek.repository;

import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.enums.Rola;

import java.util.List;
import java.util.Optional;

/**
 * Repozytorium użytkowników (Klient/Instruktor/Administrator) - polimorficzne,
 * przechowuje wszystkie podtypy Uzytkownik w jednej "tabeli"..
 */
public class UzytkownikRepository extends InMemoryRepository<Uzytkownik> {

    @Override
    protected Long pobierzId(Uzytkownik encja) {
        return encja.getId();
    }

    @Override
    protected void ustawId(Uzytkownik encja, Long id) {
        encja.setId(id);
    }

    public Optional<Uzytkownik> znajdzPoEmail(String email) {
        return znajdzWszystkie().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Uzytkownik> znajdzPoRoli(Rola rola) {
        return znajdzWszystkie().stream()
                .filter(u -> u.getRola() == rola)
                .toList();
    }

    public List<Uzytkownik> znajdzPoRolach(List<Rola> role) {
        return znajdzWszystkie().stream()
                .filter(u -> role.contains(u.getRola()))
                .toList();
    }
}
