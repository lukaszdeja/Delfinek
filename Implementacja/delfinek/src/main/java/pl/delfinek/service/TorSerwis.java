package pl.delfinek.service;

import pl.delfinek.model.Tor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serwis odpowiedzialny za zarządzanie torami basenowymi: dostępność i remonty.
 */
public interface TorSerwis {

    List<Tor> pobierzTory();

    boolean sprawdzDostepnosc(Long idToru, LocalDateTime od, LocalDateTime doCzasu);

    void zglosRemont(Long idToru, LocalDate od, LocalDate doCzasu);

    void zakonczRemont(Long idToru);
}
