package pl.delfinek.model.enums;

/**
 * Cykliczność zajęć - określa, czy zajęcia powtarzają się i z jaką częstotliwością.
 * Wykorzystywane przy generowaniu terminów
 * oraz przy usuwaniu/anulowaniu "cyklicznym".
 */
public enum Cyklicznosc {
    JEDNORAZOWE,
    CO_TYDZIEN,
    CO_DWA_TYGODNIE
}
