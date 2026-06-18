package pl.delfinek.service.impl;

import pl.delfinek.service.MailSerwis;

import java.util.List;
import java.util.logging.Logger;

/**
 * Implementacja MailSerwis - symuluje wysyłkę przez zapis do logu.
 */
public class MailSerwisImpl implements MailSerwis {

    private static final Logger LOG = Logger.getLogger(MailSerwisImpl.class.getName());

    @Override
    public void wyslijEmail(String adresat, String temat, String tresc) {
        if (adresat == null || adresat.isBlank()) {
            LOG.warning("Próba wysłania e-maila bez adresata - operacja zignorowana.");
            return;
        }
        LOG.info(() -> "[SYMULACJA WYSYŁKI E-MAIL] do=" + adresat + ", temat='" + temat + "', tresc='" + tresc + "'");
    }

    @Override
    public void wyslijEmailMasowy(List<String> adresy, String temat, String tresc) {
        if (adresy == null || adresy.isEmpty()) {
            LOG.warning("Próba wysyłki masowej bez listy adresatów - operacja zignorowana.");
            return;
        }
        LOG.info(() -> "[SYMULACJA WYSYŁKI MASOWEJ] liczba adresatów=" + adresy.size() + ", temat='" + temat + "'");
        for (String adres : adresy) {
            wyslijEmail(adres, temat, tresc);
        }
    }
}
