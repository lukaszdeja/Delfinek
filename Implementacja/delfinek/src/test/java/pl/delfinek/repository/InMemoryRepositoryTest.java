package pl.delfinek.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.delfinek.model.Uzytkownik;
import pl.delfinek.model.enums.Rola;

class InMemoryRepositoryTest {

    private TestRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TestRepository();
    }

    @Test
    void shouldSaveNewEntity() {
        TestEntity entity = new TestEntity(null, "Test");
        TestEntity saved = repository.zapisz(entity);
        
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getId());
        assertEquals("Test", saved.getName());
    }

    @Test
    void shouldUpdateExistingEntity() {
        TestEntity entity = new TestEntity(null, "Initial");
        TestEntity saved = repository.zapisz(entity);
        
        saved.setName("Updated");
        TestEntity updated = repository.zapisz(saved);
        
        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated", updated.getName());
    }

    @Test
    void shouldFindById() {
        TestEntity entity = new TestEntity(null, "Test");
        TestEntity saved = repository.zapisz(entity);
        
        Optional<TestEntity> found = repository.znajdzPoId(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        Optional<TestEntity> found = repository.znajdzPoId(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAll() {
        repository.zapisz(new TestEntity(null, "First"));
        repository.zapisz(new TestEntity(null, "Second"));
        
        List<TestEntity> all = repository.znajdzWszystkie();
        assertEquals(2, all.size());
    }

    @Test
    void shouldDelete() {
        TestEntity entity = new TestEntity(null, "ToDelete");
        TestEntity saved = repository.zapisz(entity);
        
        repository.usun(saved.getId());
        Optional<TestEntity> found = repository.znajdzPoId(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldCheckExistence() {
        TestEntity entity = new TestEntity(null, "Test");
        TestEntity saved = repository.zapisz(entity);
        
        assertTrue(repository.istnieje(saved.getId()));
        assertFalse(repository.istnieje(999L));
    }

    // Helper classes for testing
    private static class TestEntity {
        private Long id;
        private String name;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    private static class TestRepository extends InMemoryRepository<TestEntity> {
        @Override
        protected Long pobierzId(TestEntity encja) {
            return encja.getId();
        }

        @Override
        protected void ustawId(TestEntity encja, Long id) {
            encja.setId(id);
        }
    }
}
