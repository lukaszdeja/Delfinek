package pl.delfinek.service.impl;

import pl.delfinek.exception.ValidationException;
import pl.delfinek.model.Klient;
import pl.delfinek.model.Powiadomienie;
import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.Zajecia;
import pl.delfinek.model.Zapis;
import pl.delfinek.model.enums.Rola;
import pl.delfinek.model.enums.TypPowiadomienia;
import pl.delfinek.repository.PowiadomienieRepository;
import pl.delfinek.repository.UzytkownikRepository;
import pl.delfinek.service.MailSerwis;
import pl.delfinek.service.PowiadomienieSerwis;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacja PowiadomienieSerwis. Tworzy powiadomienia w "bazie"
 * dla każdego odbiorcy oraz - dla wybranych typów zdarzeń,
 * wysyła równolegle e-mail przez MailSerwis.
 */
public class PowiadomienieSerwisImpl implements PowiadomienieSerwis {

    private final PowiadomienieRepository powiadomienieRepository;
    private final UzytkownikRepository uzytkownikRepository;
    private final MailSerwis mailSerwis;

    public PowiadomienieSerwisImpl(PowiadomienieRepository powiadomienieRepository,
                                   UzytkownikRepository uzytkownikRepository, MailSerwis mailSerwis) {
        this.powiadomienieRepository = powiadomienieRepository;
        this.uzytkownikRepository = uzytkownikRepository;
        this.mailSerwis = mailSerwis;
    }

    @Override
    public void wyslij(TypPowiadomienia typ, List<Uzytkownik> odbiorcy, String tresc) {
        if (odbiorcy == null || odbiorcy.isEmpty()) {
            return;
        }
        String tytul = tytulDlaTypu(typ);
        for (Uzytkownik odbiorca : odbiorcy) {
            Powiadomienie powiadomienie = new Powiadomienie(null, tytul, tresc, typ, odbiorca);
            powiadomienieRepository.zapisz(powiadomienie);
            odbiorca.dodajPowiadomienie(powiadomienie);
        }
    }

    @Override
    public void powiadomOZmianieHarmonogramu(Zajecia zajecia) {
        if (zajecia == null) {
            throw new IllegalArgumentException("Zajęcia nie mogą być null.");
        }
        List<Uzytkownik> odbiorcy = new ArrayList<>();
        if (zajecia.getInstruktor() != null) {
            odbiorcy.add(zajecia.getInstruktor());
        }
        odbiorcy.addAll(zajecia.getListaUczestnikow());

        String tresc = "Zmieniono termin zajęć: " + zajecia.getTypZajec() + " w dniu " + zajecia.getDataGodzina();

        for (Uzytkownik odbiorca : odbiorcy) {
            Powiadomienie powiadomienie = new Powiadomienie(
                    null, tytulDlaTypu(TypPowiadomienia.ZMIANA_HARMONOGRAMU), tresc,
                    TypPowiadomienia.ZMIANA_HARMONOGRAMU, odbiorca);
            powiadomienieRepository.zapisz(powiadomienie);
            odbiorca.dodajPowiadomienie(powiadomienie);

            mailSerwis.wyslijEmail(odbiorca.getEmail(), "Zmiana w harmonogramie zajęć", tresc);
        }
    }

    @Override
    public void powiadomONowymZapisie(Zapis zapis) {
        if (zapis == null) {
            throw new IllegalArgumentException("Zapis nie może być null.");
        }
        Klient klient = zapis.getKlient();
        String tresc = "Potwierdzenie zapisu na zajęcia: " + zapis.getZajecia().getTypZajec()
                + " w dniu " + zapis.getZajecia().getDataGodzina();

        Powiadomienie powiadomienie = new Powiadomienie(
                null, tytulDlaTypu(TypPowiadomienia.POTWIERDZENIE_ZAPISU), tresc,
                TypPowiadomienia.POTWIERDZENIE_ZAPISU, klient);
        powiadomienieRepository.zapisz(powiadomienie);
        klient.dodajPowiadomienie(powiadomienie);

        mailSerwis.wyslijEmail(klient.getEmail(), "Potwierdzenie zapisu na zajęcia", tresc);
    }

    @Override
    public void powiadomONowymKursancie(Zajecia zajecia, Klient klient) {
        if (zajecia == null || klient == null) {
            throw new IllegalArgumentException("Zajęcia i klient nie mogą być null.");
        }
        if (zajecia.getInstruktor() == null) {
            return;
        }
        String tresc = "Nowy kursant (" + klient.getPelneImie() + ") zapisał się na zajęcia: "
                + zajecia.getTypZajec() + " w dniu " + zajecia.getDataGodzina();

        Powiadomienie powiadomienie = new Powiadomienie(
                null, tytulDlaTypu(TypPowiadomienia.NOWY_KURSANT), tresc,
                TypPowiadomienia.NOWY_KURSANT, zajecia.getInstruktor());
        powiadomienieRepository.zapisz(powiadomienie);
        zajecia.getInstruktor().dodajPowiadomienie(powiadomienie);
    }

    /**
     * Wysyła komunikat do grupy odbiorców wybranych po ich roli.
     */
    @Override
    public int wyslijKomunikat(String tresc, List<Rola> grupaOdbiorcow) {
        if (tresc == null || tresc.isBlank()) {
            throw new ValidationException("Treść komunikatu nie może być pusta.");
        }

        List<Uzytkownik> odbiorcy = (grupaOdbiorcow == null || grupaOdbiorcow.isEmpty())
                ? uzytkownikRepository.znajdzWszystkie()
                : uzytkownikRepository.znajdzPoRolach(grupaOdbiorcow);

        if (odbiorcy.isEmpty()) {
            return 0;
        }

        for (Uzytkownik odbiorca : odbiorcy) {
            Powiadomienie powiadomienie = new Powiadomienie(
                    null, tytulDlaTypu(TypPowiadomienia.KOMUNIKAT_OGOLNY), tresc,
                    TypPowiadomienia.KOMUNIKAT_OGOLNY, odbiorca);
            powiadomienieRepository.zapisz(powiadomienie);
            odbiorca.dodajPowiadomienie(powiadomienie);
        }

        List<String> adresy = odbiorcy.stream()
                .map(Uzytkownik::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .toList();
        mailSerwis.wyslijEmailMasowy(adresy, "Komunikat ze szkoły pływania \"Delfinek\"", tresc);

        return odbiorcy.size();
    }

    private String tytulDlaTypu(TypPowiadomienia typ) {
        return switch (typ) {
            case ZMIANA_HARMONOGRAMU -> "Zmiana harmonogramu";
            case NOWE_ZAJECIA -> "Nowe zajęcia";
            case ANULOWANIE_ZAJEC -> "Zajęcia odwołane";
            case NOWY_KURSANT -> "Nowy kursant";
            case NOWA_WIADOMOSC -> "Nowa wiadomość";
            case KOMUNIKAT_OGOLNY -> "Komunikat";
            case POTWIERDZENIE_ZAPISU -> "Potwierdzenie zapisu";
            case ANULOWANIE_ZAPISU -> "Anulowanie zapisu";
            case REMONT_OBIEKTU -> "Remont obiektu";
        };
    }
}
