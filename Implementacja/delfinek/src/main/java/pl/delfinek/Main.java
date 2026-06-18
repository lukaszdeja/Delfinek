package pl.delfinek;

import pl.delfinek.dto.RegistrationDTO;
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
    }
}
