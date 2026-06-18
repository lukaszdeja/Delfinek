package pl.delfinek.service.impl;

import pl.delfinek.dto.RegistrationDTO;
import pl.delfinek.exception.NotFoundException;
import pl.delfinek.exception.ValidationException;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.enums.Rola;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.service.MailSerwis;
import pl.delfinek.service.UzytkownikSerwis;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementacja UzytkownikSerwis. Logika zmiany roli: pobranie użytkownika, aktualizacja roli, zapis.
 *
 * Reset hasła: w realnym systemie kod resetujący byłby zapisany w bazie
 * z czasem wygaśnięcia; tutaj przechowujemy go w prostej mapie w pamięci.
 */
public class UzytkownikSerwisImpl implements UzytkownikSerwis {

    private final UzytkownikRepository uzytkownikRepository;
    private final MailSerwis mailSerwis;

    /** kod resetu hasła -> email użytkownika, którego dotyczy */
    private final Map<String, String> kodyResetuHasla = new ConcurrentHashMap<>();

    public UzytkownikSerwisImpl(UzytkownikRepository uzytkownikRepository, MailSerwis mailSerwis) {
        this.uzytkownikRepository = uzytkownikRepository;
        this.mailSerwis = mailSerwis;
    }

    @Override
    public Klient rejestruj(RegistrationDTO dto) {
        walidujRejestracje(dto);

        uzytkownikRepository.znajdzPoEmail(dto.getEmail()).ifPresent(istniejacy -> {
            throw new ValidationException("Użytkownik z podanym adresem e-mail już istnieje.");
        });

        Klient klient = new Klient(
                null, dto.getImie(), dto.getNazwisko(), dto.getEmail(), dto.getHaslo(),
                dto.getNrTelefonu(), dto.getDataUrodzenia());

        uzytkownikRepository.zapisz(klient);
        mailSerwis.wyslijEmail(klient.getEmail(), "Witamy w Delfinku!",
                "Dziękujemy za rejestrację, " + klient.getImie() + "!");
        return klient;
    }

    @Override
    public Uzytkownik znajdzPoId(Long id) {
        return uzytkownikRepository.znajdzPoId(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika o id=" + id));
    }

    @Override
    public void zmienRole(Long id, Rola rola) {
        Uzytkownik uzytkownik = znajdzPoId(id);
        if (rola == null) {
            throw new ValidationException("Rola nie może być null.");
        }
        uzytkownik.setRola(rola);
        uzytkownikRepository.zapisz(uzytkownik);
    }

    @Override
    public void resetujHaslo(String email) {
        Uzytkownik uzytkownik = uzytkownikRepository.znajdzPoEmail(email)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika o e-mailu=" + email));

        String kod = UUID.randomUUID().toString().substring(0, 8);
        kodyResetuHasla.put(kod, email);

        mailSerwis.wyslijEmail(uzytkownik.getEmail(), "Reset hasła - Delfinek",
                "Twój kod resetujący hasło: " + kod);
    }

    @Override
    public void zmienHasloPoReset(String email, String kod, String noweHaslo) {
        String zapisanyEmail = kodyResetuHasla.get(kod);
        if (zapisanyEmail == null || !zapisanyEmail.equalsIgnoreCase(email)) {
            throw new ValidationException("Nieprawidłowy lub wygasły kod resetujący.");
        }
        Uzytkownik uzytkownik = uzytkownikRepository.znajdzPoEmail(email)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika o e-mailu=" + email));

        uzytkownik.resetujHaslo(noweHaslo);
        uzytkownikRepository.zapisz(uzytkownik);
        kodyResetuHasla.remove(kod);
    }

    private void walidujRejestracje(RegistrationDTO dto) {
        if (dto == null) {
            throw new ValidationException("Dane rejestracji nie mogą być null.");
        }
        if (dto.getImie() == null || dto.getImie().isBlank()) {
            throw new ValidationException("Imię jest wymagane.");
        }
        if (dto.getNazwisko() == null || dto.getNazwisko().isBlank()) {
            throw new ValidationException("Nazwisko jest wymagane.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank() || !dto.getEmail().contains("@")) {
            throw new ValidationException("Adres e-mail jest nieprawidłowy.");
        }

        //narazie sprawdzamy tylko po dlugosci, trzeba dodac jakis regex
        if (dto.getHaslo() == null || dto.getHaslo().length() < 6) {
            throw new ValidationException("Hasło musi mieć co najmniej 6 znaków.");
        }
    }
}
