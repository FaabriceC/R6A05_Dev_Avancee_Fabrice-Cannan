package com.master.air.repository;

import com.master.air.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AnnonceRepositoryIT {

    private static EntityManagerFactory emf;
    private EntityManager em;

    private static User testUser;
    private static Category testCat;

    @BeforeAll
    static void setupClass() {
        emf = Persistence.createEntityManagerFactory("MasterAnnonceTestPU");

        EntityManager initEm = emf.createEntityManager();
        initEm.getTransaction().begin();

        testUser = new User("testuser", "test@test.com", "pass");
        initEm.persist(testUser);

        testCat = new Category("TestCategory");
        initEm.persist(testCat);

        for (int i = 0; i < 10; i++) {
            Annonce a = new Annonce();
            a.setTitle("Annonce test " + i);
            a.setDescription("Description de l'annonce " + i);
            a.setAdress("Adresse " + i);
            a.setMail("test" + i + "@test.com");
            a.setStatus(i < 5 ? AnnonceStatus.DRAFT : AnnonceStatus.PUBLISHED);
            a.setAuthor(testUser);
            a.setCategory(testCat);
            initEm.persist(a);
        }

        initEm.getTransaction().commit();
        initEm.close();
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

    @Test
    @DisplayName("CRUD - Create and FindById")
    void testCreateAndFind() {
        em.getTransaction().begin();
        AnnonceRepository repo = new AnnonceRepository(em);
        Annonce a = new Annonce();
        a.setTitle("Nouvelle");
        a.setDescription("Desc");
        a.setAdress("Addr");
        a.setMail("m@t.com");
        a.setStatus(AnnonceStatus.DRAFT);
        a.setAuthor(em.find(User.class, testUser.getId()));
        a.setCategory(em.find(Category.class, testCat.getId()));
        repo.create(a);
        em.getTransaction().commit();

        assertNotNull(a.getId());
        assertTrue(repo.findById(a.getId()).isPresent());
    }

    @Test
    @DisplayName("Pagination - page 1 size 3")
    void testPagination() {
        AnnonceRepository repo = new AnnonceRepository(em);
        PaginatedResult<Annonce> page = repo.findAllPaginated(1, 3);

        assertEquals(3, page.getItems().size());
        assertTrue(page.getTotalCount() >= 10);
        assertTrue(page.hasNext());
    }

    @Test
    @DisplayName("Pagination - page 2")
    void testPaginationPage2() {
        AnnonceRepository repo = new AnnonceRepository(em);
        PaginatedResult<Annonce> page = repo.findAllPaginated(2, 3);

        assertEquals(3, page.getItems().size());
        assertTrue(page.hasPrevious());
    }

    @Test
    @DisplayName("Search by keyword")
    void testSearchByKeyword() {
        AnnonceRepository repo = new AnnonceRepository(em);
        PaginatedResult<Annonce> result = repo.searchByKeyword("Annonce test", 1, 20);

        assertTrue(result.getTotalCount() >= 10);
    }

    @Test
    @DisplayName("Filter by status PUBLISHED")
    void testFilterByStatus() {
        AnnonceRepository repo = new AnnonceRepository(em);
        PaginatedResult<Annonce> result = repo.findByFilter(null, AnnonceStatus.PUBLISHED, 1, 20);

        assertTrue(result.getTotalCount() >= 5);
        result.getItems().forEach(a -> assertEquals(AnnonceStatus.PUBLISHED, a.getStatus()));
    }

    @Test
    @DisplayName("Filter by category")
    void testFilterByCategory() {
        AnnonceRepository repo = new AnnonceRepository(em);
        PaginatedResult<Annonce> result = repo.findByFilter(testCat.getId(), null, 1, 20);

        assertTrue(result.getTotalCount() >= 10);
    }

    @Test
    @DisplayName("FindByIdWithRelations - no LazyInit")
    void testFindByIdWithRelations() {
        AnnonceRepository repo = new AnnonceRepository(em);
        PaginatedResult<Annonce> all = repo.findAllPaginated(1, 1);
        Long id = all.getItems().get(0).getId();

        var opt = repo.findByIdWithRelations(id);
        assertTrue(opt.isPresent());
        assertNotNull(opt.get().getAuthor().getUsername());
        assertNotNull(opt.get().getCategory().getLabel());
    }

    @Test
    @DisplayName("CRUD - Update")
    void testUpdate() {
        em.getTransaction().begin();
        AnnonceRepository repo = new AnnonceRepository(em);
        PaginatedResult<Annonce> all = repo.findAllPaginated(1, 1);
        Annonce a = all.getItems().get(0);
        a.setTitle("Modified Title");
        repo.update(a);
        em.getTransaction().commit();

        Annonce updated = repo.findById(a.getId()).orElseThrow();
        assertEquals("Modified Title", updated.getTitle());
    }

    @Test
    @DisplayName("CRUD - Delete")
    void testDelete() {
        em.getTransaction().begin();
        AnnonceRepository repo = new AnnonceRepository(em);
        Annonce a = new Annonce();
        a.setTitle("ToDelete");
        a.setDescription("D");
        a.setAdress("A");
        a.setMail("d@t.com");
        a.setStatus(AnnonceStatus.DRAFT);
        a.setAuthor(em.find(User.class, testUser.getId()));
        a.setCategory(em.find(Category.class, testCat.getId()));
        repo.create(a);
        em.getTransaction().commit();

        Long id = a.getId();
        em.getTransaction().begin();
        repo.delete(repo.findById(id).orElseThrow());
        em.getTransaction().commit();

        assertTrue(repo.findById(id).isEmpty());
    }
}
