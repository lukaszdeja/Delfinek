package pl.delfinek.service;

import java.util.List;

/**
 * Serwis odpowiedzialny za wysyłkę e-maili. Ponieważ nie mamy bazy danych to
 * jedynie loguje informację o "wysłanym" mailu.
 */
public interface MailSerwis {

    void wyslijEmail(String adresat, String temat, String tresc);

    void wyslijEmailMasowy(List<String> adresy, String temat, String tresc);
}
