package pl.delfinek.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstruktorTest {

    private Instruktor instruktor;

    @BeforeEach
    void setUp() {
        instruktor = new Instruktor(1L, "Piotr", "Nowak", "piotr@example.com", "pass",
                "123", LocalDate.of(1990, 1, 1), "Początkowy opis");
    }

    @Test
    void shouldEditDescription() {
        instruktor.edytujSwojOpis("Nowy opis");
        assertEquals("Nowy opis", instruktor.getOpis());
    }

    @Test
    void shouldHaveCorrectRole() {
        assertEquals(pl.delfinek.model.enums.Rola.INSTRUKTOR, instruktor.getRola());
    }
}
