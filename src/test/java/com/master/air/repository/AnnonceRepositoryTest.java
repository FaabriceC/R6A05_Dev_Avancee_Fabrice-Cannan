package com.master.air.repository;

import com.master.air.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AnnonceRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void setupClass() {
        emf = Persistence.createEntityManagerFactory("MasterAnnonceTestPU");
    }

    @AfterAll
    static void teardownClass() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
    }

    @AfterEach
    void teardown() {
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    private User createUser(String name) {
        User u = new User(name, name + "@test.com", "pass");
        em.persist(u);
        return u;
    }

    private Category createCat(String label) {
        Category c = new Category(label);
        em.persist(c);
        return c;
    }

    private Annonce createAnnonce(User u, Category c, String title, AnnonceStatus st) {
        Annonce a = new Annonce();
        a.setTitle(title);
        a.setDescription("Desc " + title);
        a.setAdress("123 rue Test");
        a.setMail("t@t.com");
        a.setStatus(st);
        a.setAuthor(u);
        a.setCategory(c);
        em.persist(a);
        return a;
    }

    @Test
    @DisplayName("CRUD - Create and FindById")
    void testCreateAndFind() {
        em.getTransaction().begin();
        User u = createUser("u1");
        Category c = createCat("C1");
        AnnonceRepository repo = new AnnonceRepository(em);

        Annonce a = new Annonce();
        a.setTitle("Test");
        a.setDescription("Desc");
        a.setAdress("Addr");
        a.setMail("m@t.com");
        a.setStatus(AnnonceStatus.DRAFT);
        a.setAuthor(u);
        a.setCategory(c);
        repo.create(a);
        em.getTransaction().commit();

        assertNotNull(a.getId());
        assertTrue(repo.findById(a.getId()).isPresent());
    }

    @Test
    @DisplayName("CRUD - Update")
    void testUpdate() {
        em.getTransaction().begin();
        User u = createUser("u2");
        Category c = createCat("C2");
        Annonce a = createAnnonce(u, c, "Original", AnnonceStatus.DRAFT);
        em.getTransaction().commit();

        em.getTransaction().begin();
        AnnonceRepository repo = new AnnonceRepository(em);
        Annonce found = repo.findById(a.getId()).orElseThrow();
        found.setTitle("Modified");
        repo.update(found);
        em.getTransaction().commit();

        assertEquals("Modified", repo.findById(a.getId()).get().getTitle());
    }

    @Test
    @DisplayName("CRUD - Delete")
    void testDelete() {
        em.getTransaction().begin();
        User u = createUser("u3");
        Category c = createCat("C3");
        Annonce a = createAnnonce(u, c, "ToDelete", AnnonceStatus.DRAFT);
        em.getTransaction().commit();
        Long id = a.getId();

        em.getTransaction().begin();
        AnnonceRepository repo = new AnnonceRepository(em);
        repo.delete(repo.findById(id).orElseThrow());
        em.getTransaction().commit();

        assertTrue(repo.findById(id).isEmpty());
    }

    @Test
    @DisplayName("Search by keyword")
    void testSearch() {
        em.getTransaction().begin();
        User u = createUser("u4");
        Category c = createCat("C4");
        createAnnonce(u, c, "Appartement lumineux", AnnonceStatus.PUBLISHED);
        createAnnonce(u, c, "Voiture occasion", AnnonceStatus.PUBLISHED);
        createAnnonce(u, c, "Bel appartement", AnnonceStatus.DRAFT);
        em.getTransaction().commit();

        PaginatedResult<Annonce> r = new AnnonceRepository(em).searchByKeyword("appartement", 1, 10);
        assertEquals(2, r.getTotalCount());
    }

    @Test
    @DisplayName("Pagination")
    void testPagination() {
        em.getTransaction().begin();
        User u = createUser("u5");
        Category c = createCat("C5");
        for (int i = 0; i < 7; i++) createAnnonce(u, c, "A" + i, AnnonceStatus.DRAFT);
        em.getTransaction().commit();

        PaginatedResult<Annonce> p = new AnnonceRepository(em).findAllPaginated(1, 3);
        assertEquals(3, p.getItems().size());
        assertTrue(p.hasNext());
    }

    @Test
    @DisplayName("Filter by category and status")
    void testFilter() {
        em.getTransaction().begin();
        User u = createUser("u6");
        Category c1 = createCat("CA");
        Category c2 = createCat("CB");
        createAnnonce(u, c1, "A1", AnnonceStatus.PUBLISHED);
        createAnnonce(u, c1, "A2", AnnonceStatus.DRAFT);
        createAnnonce(u, c2, "B1", AnnonceStatus.PUBLISHED);
        em.getTransaction().commit();

        PaginatedResult<Annonce> r = new AnnonceRepository(em).findByFilter(c1.getId(), AnnonceStatus.PUBLISHED, 1, 10);
        assertEquals(1, r.getTotalCount());
    }
}
