package pl.delfinek;

import pl.delfinek.dto.RegistrationDTO;
import pl.delfinek.dto.ZajeciaDTO;
import pl.delfinek.model.*;
import pl.delfinek.model.enums.*;
import pl.delfinek.repository.*;
import pl.delfinek.service.*;
import pl.delfinek.service.impl.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

/**
 * Klasa demonstracyjna.
 */
public class Main {

    public static void main(String[] args) {
        // Repozytoria
        UzytkownikRepository uzytkownikRepository = new UzytkownikRepository();
        ZajeciaRepository zajeciaRepository = new ZajeciaRepository();
        ZapisRepository zapisRepository = new ZapisRepository();
        TorRepository torRepository = new TorRepository();
        PowiadomienieRepository powiadomienieRepository = new PowiadomienieRepository();

        // Serwisy
        MailSerwis mailSerwis = new MailSerwisImpl();
        PowiadomienieSerwis powiadomienieSerwis = new PowiadomienieSerwisImpl(powiadomienieRepository, uzytkownikRepository, mailSerwis);
        TorSerwis torSerwis = new TorSerwisImpl(torRepository, zajeciaRepository);
        UzytkownikSerwis uzytkownikSerwis = new UzytkownikSerwisImpl(uzytkownikRepository, mailSerwis);
        ZapisSerwis zapisSerwis = new ZapisSerwisImpl(zapisRepository, zajeciaRepository, uzytkownikRepository, powiadomienieSerwis);
        HarmonogramSerwis harmonogramSerwis = new HarmonogramSerwisImpl(
                zajeciaRepository, zapisRepository, torRepository, uzytkownikRepository, torSerwis, powiadomienieSerwis);


        System.out.println("--- 1. Inicjalizacja danych podstawowych ---");

        Administrator admin = new Administrator(
                null, "Anna", "Kowalska", "admin@delfinek.pl", "admin123",
                "111111111", LocalDate.of(1985, 1, 1));
        uzytkownikRepository.zapisz(admin);

        Instruktor instruktor = new Instruktor(
                null, "Piotr", "Nowak", "piotr.nowak@gmail.com", "instr123",
                "222222222", LocalDate.of(1990, 5, 12), "Instruktor pływania z 10-letnim stażem.");
        uzytkownikRepository.zapisz(instruktor);

        Tor tor1 = torRepository.zapisz(new Tor(null, 1, 25, 1.8));
        Tor tor2 = torRepository.zapisz(new Tor(null, 2, 25, 1.2));

        System.out.println("Dodano administratora: " + admin);
        System.out.println("Dodano instruktora: " + instruktor);
        System.out.println("Dodano tory: " + tor1 + ", " + tor2);

        System.out.println();
        System.out.println("--- 2. Rejestracja klientów ---");

        Klient klient1 = uzytkownikSerwis.rejestruj(new RegistrationDTO(
                "Marta", "Wiśniewska", "marta@gmail.com", "haslo123",
                "123456789", LocalDate.of(2014, 3, 20)));
        Klient klient2 = uzytkownikSerwis.rejestruj(new RegistrationDTO(
                "Jan", "Zieliński", "jan@gmail.com", "haslo456",
                "676767676", LocalDate.of(2012, 7, 9)));

        System.out.println("Zarejestrowano klienta: " + klient1);
        System.out.println("Zarejestrowano klienta: " + klient2);

        System.out.println();

        System.out.println("--- 3. Dodanie zajęć cyklicznych ---");

        ZajeciaDTO dto = new ZajeciaDTO(
                LocalDateTime.of(2026, Month.JULY, 6, 16, 0),
                45, TypZajec.DZIECI_GR_A, instruktor.getId(), tor1.getId(),
                6, "Grupa dla dzieci 8-10 lat", Cyklicznosc.CO_TYDZIEN,
                LocalDate.of(2026, Month.JULY, 27));

        List<Zajecia> wygenerowaneZajecia = harmonogramSerwis.dodajZajecia(dto);
        System.out.println("Wygenerowano " + wygenerowaneZajecia.size() + " terminów zajęć (co tydzień):");
        wygenerowaneZajecia.forEach(z -> System.out.println("  -> " + z));

        System.out.println();
        System.out.println("--- 4. Próba dodania kolidujących zajęć na tym samym torze (oczekiwany ConflictException) ---");
        try {
            ZajeciaDTO kolidujace = new ZajeciaDTO(
                    LocalDateTime.of(2026, Month.JULY, 6, 16, 20),
                    30, TypZajec.DOROSLI_PODSTAWOWY, null, tor1.getId(), 4, null,
                    Cyklicznosc.JEDNORAZOWE, null);
            harmonogramSerwis.dodajZajecia(kolidujace);
        } catch (Exception e) {
            System.out.println("Złapano wyjątek: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- 5. Zapis klientów na pierwsze zajęcia ---");

        Zajecia pierwszeZajecia = wygenerowaneZajecia.get(0);
        Zapis zapis1 = zapisSerwis.zapisz(klient1.getId(), pierwszeZajecia.getId());
        Zapis zapis2 = zapisSerwis.zapisz(klient2.getId(), pierwszeZajecia.getId());

        System.out.println("Zapis 1: " + zapis1);
        System.out.println("Zapis 2: " + zapis2);
        System.out.println("Wolne miejsca na zajęciach: " + pierwszeZajecia.getLiczbaMiejscWolnych()
                + " / " + pierwszeZajecia.getMaxLiczbaMiejsc());

        System.out.println();
        System.out.println("--- 6. Anulowanie zapisu ---");
        zapisSerwis.anuluj(zapis2.getId(), false);
        System.out.println("Po anulowaniu zapisu klienta 2, wolne miejsca: "
                + pierwszeZajecia.getLiczbaMiejscWolnych() + " / " + pierwszeZajecia.getMaxLiczbaMiejsc());

        System.out.println();
        System.out.println("--- 7. Edycja zajęć - zmiana godziny ---");
        System.out.println("Zajęcia przed edycją: " + pierwszeZajecia);
        ZajeciaDTO edycjaDto = new ZajeciaDTO(pierwszeZajecia.getDataGodzina().plusHours(1),
                pierwszeZajecia.getCzasTrwaniaMin(), pierwszeZajecia.getTypZajec(),
                instruktor.getId(), tor1.getId(), pierwszeZajecia.getMaxLiczbaMiejsc(),
                "Zmiana godziny zajęć na prośbę instruktora o godzinę", pierwszeZajecia.getCyklicznosc(), pierwszeZajecia.getDataKonca());
        Zajecia zedytowane = harmonogramSerwis.edytujZajecia(pierwszeZajecia.getId(), edycjaDto);
        System.out.println("Zajęcia po edycji: " + zedytowane);

        System.out.println();
        System.out.println("--- 8. Powiadomienia klienta 1 po edycji ---");
        klient1.pobierzPowiadomienia().forEach(p ->
                System.out.println("  [" + p.getTyp() + "] " + p.getTresc()));

        System.out.println();
    }
}
