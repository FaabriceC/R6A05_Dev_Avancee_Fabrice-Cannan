package com.master.air.service;

import com.master.air.exception.ConflictException;
import com.master.air.exception.ForbiddenException;
import com.master.air.exception.NotFoundException;
import com.master.air.model.Annonce;
import com.master.air.model.AnnonceStatus;
import com.master.air.model.Category;
import com.master.air.model.User;
import com.master.air.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AnnonceServiceTest {

    private static final String TEST_PU = "MasterAnnonceTestPU";

    private final AnnonceService service = new AnnonceService();

    @BeforeAll
    static void beforeAll() {
        System.setProperty("masterannonce.persistenceUnit", TEST_PU);
        JPAUtil.close();
    }

    @AfterAll
    static void afterAll() {
        JPAUtil.close();
        System.clearProperty("masterannonce.persistenceUnit");
    }


    private User persistUser(String username) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User u = new User(username, username + "@test.com", "pass");
            em.persist(u);
            em.getTransaction().commit();
            return u;
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    private Category persistCategory(String label) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Category c = new Category(label);
            em.persist(c);
            em.getTransaction().commit();
            return c;
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    private Annonce persistAnnonce(User author, Category cat, AnnonceStatus status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            User managedUser = em.find(User.class, author.getId());
            Category managedCat = em.find(Category.class, cat.getId());

            Annonce a = new Annonce();
            a.setTitle("Titre");
            a.setDescription("Description");
            a.setAdress("Adresse");
            a.setMail("mail@test.com");
            a.setStatus(status);
            a.setAuthor(managedUser);
            a.setCategory(managedCat);
            em.persist(a);

            em.getTransaction().commit();
            return a;
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }


    @Test
    @DisplayName("Règle métier: seul l'auteur peut modifier")
    void onlyAuthorCanUpdate() {
        User author = persistUser("alice");
        User other = persistUser("bob");
        Category cat = persistCategory("Cat");
        Annonce annonce = persistAnnonce(author, cat, AnnonceStatus.DRAFT);

        assertThrows(ForbiddenException.class, () ->
                service.updateAnnonce(
                        annonce.getId(),
                        "Nouveau titre",
                        "Nouvelle description",
                        "Nouvelle adresse",
                        "new@test.com",
                        cat.getId(),
                        other.getId()
                )
        );
    }

    @Test
    @DisplayName("Règle métier: une annonce PUBLISHED ne peut pas être modifiée")
    void publishedCannotBeUpdated() {
        User author = persistUser("alice2");
        Category cat = persistCategory("Cat2");
        Annonce annonce = persistAnnonce(author, cat, AnnonceStatus.PUBLISHED);

        assertThrows(ConflictException.class, () ->
                service.updateAnnonce(
                        annonce.getId(),
                        "Tentative titre",
                        "Tentative desc",
                        "Tentative addr",
                        "try@test.com",
                        cat.getId(),
                        author.getId()
                )
        );
    }

    @Test
    @DisplayName("Règle métier: suppression requiert ARCHIVED")
    void deleteRequiresArchived() {
        User author = persistUser("alice3");
        Category cat = persistCategory("Cat3");
        Annonce annonce = persistAnnonce(author, cat, AnnonceStatus.DRAFT);

        assertThrows(ConflictException.class, () ->
                service.deleteAnnonce(annonce.getId(), author.getId())
        );
    }

    @Test
    @DisplayName("Workflow: publish change DRAFT -> PUBLISHED")
    void publishWorkflow() {
        User author = persistUser("alice4");
        Category cat = persistCategory("Cat4");
        Annonce annonce = persistAnnonce(author, cat, AnnonceStatus.DRAFT);

        Annonce published = service.publishAnnonce(annonce.getId());
        assertEquals(AnnonceStatus.PUBLISHED, published.getStatus());

        Annonce fromDb = service.getAnnonce(annonce.getId()).orElseThrow();
        assertEquals(AnnonceStatus.PUBLISHED, fromDb.getStatus());
    }

    @Test
    @DisplayName("Workflow: archive change PUBLISHED -> ARCHIVED")
    void archiveWorkflow() {
        User author = persistUser("alice5");
        Category cat = persistCategory("Cat5");
        Annonce annonce = persistAnnonce(author, cat, AnnonceStatus.PUBLISHED);

        Annonce archived = service.archiveAnnonce(annonce.getId());
        assertEquals(AnnonceStatus.ARCHIVED, archived.getStatus());
    }

    @Test
    @DisplayName("NotFoundException si annonce inexistante")
    void notFound() {
        User author = persistUser("alice6");
        Category cat = persistCategory("Cat6");

        assertThrows(NotFoundException.class, () ->
                service.updateAnnonce(
                        999999L,
                        "Titre",
                        "Desc",
                        "Addr",
                        "m@test.com",
                        cat.getId(),
                        author.getId()
                )
        );
    }
}
