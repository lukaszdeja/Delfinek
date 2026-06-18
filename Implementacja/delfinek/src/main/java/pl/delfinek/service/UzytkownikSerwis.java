package pl.delfinek.service;

import pl.delfinek.dto.RegistrationDTO;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.enums.Rola;

/**
 * Serwis odpowiedzialny za rejestrację, wyszukiwanie i zarządzanie kontami
 * użytkowników, w tym zmianę ról i reset hasła.
 */
public interface UzytkownikSerwis {

    Klient rejestruj(RegistrationDTO dto);

    Uzytkownik znajdzPoId(Long id);

    void zmienRole(Long id, Rola rola);

    /** Inicjuje proces resetu hasła - generuje kod i wysyła e-mail z instrukcją. */
    void resetujHaslo(String email);

    void zmienHasloPoReset(String email, String kod, String noweHaslo);
}
