
# Raport z badania interfejsu użytkownika — System Delfinek

**Projekt:** Delfinek — System zarządzania szkołą pływania  
**Zespół:** Łukasz Deja, Natalia Parszywka, Miłosz Błaszczyk  
**Data badania:** Maj 2026  
**Wersja dokumentu:** 1.1

---

## 1. Cel i zakres badania

Celem badania było zweryfikowanie użyteczności prototypu lo-fi interfejsu użytkownika systemu Delfinek przed wejściem w fazę implementacji. Badanie miało sprawdzić:

- Czy nawigacja po systemie jest intuicyjna dla wszystkich grup użytkowników?
- Czy proces zapisu na zajęcia jest zrozumiały i bezproblemowy?
- Czy ekran logowania i rejestracji jest czytelny?
- Jakie problemy napotykają użytkownicy przy pierwszym kontakcie z systemem?

---

## 2. Metodologia

Badanie przeprowadziliśmy sami, metodą moderowanych testów użyteczności. Pokazaliśmy interaktywny prototyp wykonany w programie Moqups **5 osobom (studentom)**:

Uczestnicy dostali do wykonania proste zadania: 

**Zadania jako klient:**
1. Utworzenie konta i zalogowanie się
2. Wyszukanie dostępnych zajęć w nadchodzącym tygodniu
3. Zapisanie się na wybrane zajęcia
4. Sprawdzenie szczegółów zajęć (instruktor, tor, godzina)
5. Anulowanie zapisu na zajęcia
6. Skontaktowanie się z instruktorem przez czat

**Zadania jako instruktor:**
1. Zalogowanie się i przejrzenie grafiku
2. Wysłanie prośby o usunięcie istniejących zajęć
3. Przejrzenie i edytowanie informacji o kliencie
4. Zmiana hasła

**Zadania jako pracownik biura:**
1. Dodanie nowych zajęć do harmonogramu
2. Sprawdzenie dostępności torów
3. Dodanie nowych zajęć
4. Sprawdzenie szczegółów zajęć
5. Nadanie/Zmiana roli użytkownika systemu

---

## 3. Główne problemy i zaobserwowane błędy

Podczas sesji wyłapaliśmy kilka kluczowych problemów w interfejsie. Część z nich została otwarcie wskazana przez samych użytkowników, natomiast inne błędy (głównie projektowe i procesowe) zauważyliśmy my (twórcy) na podstawie obserwacji zachowania testerów.

### Problemy krytyczne (do natychmiastowej poprawy):
* **Brak potwierdzenia zapisu**: Po kliknięciu „Zapisz się” na ekranie nic się nie pojawia. Uczestnik 4 klikał przycisk kilka razy, bo nie wiedział, czy system zadziałał.
* **Brak potwierdzenia dodania zajęć przez admina** Uczestnik 1 zauważył, że po zatwierdzeniu formularza przez administratora i dodaniu nowej lekcji do grafiku, system nie wyświetla żadnego komunikatu potwierdzającego sukces. Użytkownik nie wie, czy operacja się powiodła bez ręcznego sprawdzania kalendarza. Dodatkowo zauważono, że przy dodawaniu cyklicznych zajęc powinna być dodana informacja o pominiętych terminach ze względu na dni wolne od pracy (świąteczne).
* **Brak informacji o godzinach zajęć w widoku torów dla pracownika biura**: Uczestnik 3 zauważył, że zajęcia były prezentowane bez wskazania czasu ich trwania, co mogło prowadzić do niejasności dotyczących harmonogramu. Dodano informację o godzinach zajęć.

### Mniejsze błędy i niedogodności:
* **Brak dostępu do opisu instruktora**: Podczas gdy w aktualnej wersji systemu klienci mogą już bez problemu zobaczyć opis instruktora przy logowaniu się na zajęcia (co było niemożliwe we wcześniejszej wersji), klient nie miał możliwości sprawdzenia informacji o instruktorze, do którego chciał się zapisać na zajęcia.
* **Brak możliwości napisania do kursanta**: Przy przeglądaniu informacji o kliencie z perspektywy instruktora uczestnik 3 zauważył, że brakuje przydatnej funkcji napisania do swojego kursanta, bez konieczności wybierania z listy wszystkich użytkowników systemu.
* **Brak informacji o cyklicznej rezygnacji z zajęć**: (zauważone przez twórców) Podczas usuwania lub anulowania uczestnictwa w zajęciach dostępna jest opcja cyklicznej rezygnacji. Interfejs nie informuje jednak, których konkretnie terminów dotyczy ta operacja. Aby sprawdzić, z jakich zajęć użytkownik rezygnuje, konieczne jest samodzielne przejrzenie harmonogramu.

---

## 4. Podsumowanie spostrzeżeń uczestników

Użytkownicy bez większych problemów wykonywali większość zadań i uznali podstawową nawigację systemu za intuicyjną. Podczas testów użytkownicy zwrócili jednak uwagę na niedociągnięcia w obecnym projekcie:
- Najczęściej zgłaszanym problemem był brak jasnej informacji zwrotnej po wykonaniu operacji (np. zapis na zajęcia, dodanie zajęć).
- Użytkownicy oczekiwali większej przejrzystości i dostępności informacji kontekstowych (np. opis instruktora, godziny zajęć).
- Problemy wynikały głównie z braku komunikatów systemowych, a nie z samej struktury interfejsu.
- Pomimo uwag, ogólny odbiór prototypu był pozytywny.

---

## 5. Rekomendacje zmian

Na podstawie uwag użytkowników oraz naszych własnych obserwacji wprowadzono następujące poprawki do prototypu:

- Dodanie komunikatów potwierdzających wykonanie kluczowych operacji, takich jak zapis na zajęcia, anulowanie uczestnictwa czy dodanie nowych zajęć do harmonogramu.
- Rozszerzenie informacji dostępnych dla klientów poprzez umożliwienie podglądu opisu instruktora przed zapisaniem się na zajęcia.
- Uzupełnienie widoku torów o godziny rozpoczęcia i zakończenia zajęć w celu zwiększenia czytelności harmonogramu.
- Dodanie możliwości szybkiego kontaktu instruktora z kursantem bez konieczności wyszukiwania go na liście wszystkich użytkowników.
- Wyświetlanie informacji o terminach objętych cykliczną rezygnacją z zajęć przed zatwierdzeniem operacji.
- Dodanie informacji o dodawanych terminach przez administratora jak i pominiętych podczas tworzenia harmonogramów cyklicznych obejmujących dni wolne od pracy i święta.

---

## 6. Wnioski

Przeprowadzone badanie z udziałem 5 użytkowników dostarczyło kluczowych wniosków, które pozwoliły zoptymalizować system Delfinek przed etapem wdrożenia:

- System w podstawowym zakresie okazał się intuicyjny i zrozumiały dla większości użytkowników, którzy bez większych trudności realizowali przygotowane scenariusze zadań.
- Największym problemem był brak informacji zwrotnej po wykonaniu operacji, co prowadziło do niepewności użytkowników co do skuteczności wykonanych działań.
- Istotne braki dotyczyły również dostępności informacji kontekstowych, takich jak szczegóły dotyczące instruktora, godziny zajęć czy zakres wykonywanych operacji, co utrudniało podejmowanie decyzji w systemie.
- Pomimo wskazanych niedociągnięć, ogólny odbiór prototypu był pozytywny, a struktura systemu została oceniona jako przejrzysta.
