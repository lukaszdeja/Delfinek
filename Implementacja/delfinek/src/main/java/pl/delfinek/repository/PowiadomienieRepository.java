package pl.delfinek.repository;

import pl.delfinek.model.Powiadomienie;
import pl.delfinek.model.Uzytkownik;

import java.util.List;

public class PowiadomienieRepository extends InMemoryRepository<Powiadomienie> {

    @Override
    protected Long pobierzId(Powiadomienie encja) {
        return encja.getId();
    }

    @Override
    protected void ustawId(Powiadomienie encja, Long id) {
        encja.setId(id);
    }

    public List<Powiadomienie> znajdzPoOdbiorcy(Uzytkownik odbiorca) {
        return znajdzWszystkie().stream()
                .filter(p -> p.getOdbiorca() != null && p.getOdbiorca().equals(odbiorca))
                .toList();
    }

    public List<Powiadomienie> znajdzNieprzeczytanePoOdbiorcy(Uzytkownik odbiorca) {
        return znajdzPoOdbiorcy(odbiorca).stream()
                .filter(p -> !p.isPrzeczytane())
                .toList();
    }
}
