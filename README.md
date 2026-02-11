# MasterAnnonce - TP Dev Avance #2 - JPA / Hibernate

**Auteur :** Fabrice CANNAN
**Formation :** BUT 3 Informatique
**Module :** R6A05 - Developpement Avance

---

## Architecture du projet

Le projet suit une **architecture en couches** conforme aux standards Java EE :

```
com.master.air
|-- model/          Entites JPA (User, Category, Annonce, AnnonceStatus, PaginatedResult)
|-- repository/     Couche persistence - DAO JPA (JPQL uniquement)
|-- service/        Couche metier - Gestion des transactions
|-- servlet/        Couche Web - Servlets & Listener
|-- filter/         Filtre de securite (AuthFilter)
```

### Couche Model (Entites JPA)
- **User** : utilisateur avec username, email, password, createdAt. Relation @OneToMany vers Annonce.
- **Category** : categorie avec label unique. Relation @OneToMany vers Annonce.
- **Annonce** : annonce avec title, description, adress, mail, date, status (ENUM). Relations @ManyToOne vers User et Category.
- **AnnonceStatus** : enum Java (DRAFT, PUBLISHED, ARCHIVED) mappe via @Enumerated(EnumType.STRING).
- **PaginatedResult<T>** : classe utilitaire generique pour la pagination.

### Couche Repository (DAO JPA)
- `AnnonceRepository` : CRUD + recherche par mot-cle (JPQL LIKE) + filtrage categorie/statut + pagination.
- `UserRepository` : CRUD + authentification + verification d'unicite.
- `CategoryRepository` : CRUD + recherche par label.
- **Contrainte respectee** : utilisation exclusive de JPQL, aucun JDBC.

### Couche Service (Transactions)
- `AnnonceService` : creation, modification, publication, archivage, suppression, recherche paginee.
  **Les transactions sont gerees exclusivement dans la couche service** (begin/commit/rollback).
  Aucune transaction n'est ouverte dans les Servlets.
- `UserService` : authentification et inscription.
- `CategoryService` : gestion des categories + initialisation des categories par defaut.

### Couche Web (Servlets & JSP)
- `LoginServlet` / `RegisterServlet` / `LogoutServlet` : systeme d'authentification par session.
- `AnnonceListServlet` : liste paginee avec recherche et filtrage.
- `AnnonceAddServlet` / `AnnonceUpdateServlet` : formulaires de creation et modification.
- `AnnonceDetailServlet` : vue detaillee d'une annonce.
- `AnnonceDeleteServlet` : confirmation et suppression.
- `AnnonceStatusServlet` : actions Publish / Archive.
- `AppInitListener` : initialise JPA et cree les donnees par defaut au demarrage.
- `AuthFilter` : filtre de securite qui redirige vers /login si non connecte.

---


## Problemes rencontres et solutions

### 1. LazyInitializationException
**Probleme** : En accedant a `annonce.getAuthor()` ou `annonce.getCategory()` dans les JSP, une LazyInitializationException etait levee car l'EntityManager etait deja ferme.
**Solution** : Utilisation de `JOIN FETCH` dans les requetes JPQL des repositories pour charger les relations en une seule requete. Methode dediee `findByIdWithRelations()`.

### 2. Probleme N+1
**Probleme** : La liste des annonces generait N+1 requetes (1 pour les annonces + N pour charger chaque auteur et categorie).
**Solution** : `JOIN FETCH a.author JOIN FETCH a.category` dans les requetes de listing. Une seule requete SQL est executee.

### 3. Gestion du cycle de vie de l'EntityManager
**Probleme** : Des connexions restaient ouvertes si une exception etait lancee avant em.close().
**Solution** : Pattern try/finally systematique dans tous les services pour garantir la fermeture de l'EntityManager.

### 4. Transactions dans les Servlets
**Probleme** : Les transactions etaient initialement gerees dans les Servlets, melant logique web et persistence.
**Solution** : Refactorisation pour que seule la couche Service ouvre et commette les transactions. Les Servlets appellent uniquement les methodes du service.

### 5. Pagination avec JPQL et JOIN FETCH
**Probleme** : Hibernate emet un warning "HHH90003004: firstResult/maxResults specified with collection fetch" quand on utilise pagination + JOIN FETCH.
**Solution** : Hibernate applique la pagination en memoire dans ce cas. Pour un volume de donnees faible c'est acceptable. Pour la production, on pourrait utiliser deux requetes separees (count + data) ou des sous-requetes.

### 6. Validation cote serveur et conservation des saisies
**Probleme** : Lors d'une erreur de validation, les champs du formulaire etaient vides apres redirect.
**Solution** : Forward (et non redirect) en cas d'erreur, avec reinjection des valeurs saisies via request.setAttribute().

### 7. Filtre d'authentification et boucle de redirection
**Probleme** : Le filtre AuthFilter redirigeait /login vers /login creant une boucle infinie.
**Solution** : Liste d'URLs publiques dans le filtre (login, register, css, js, images) exemptees de l'authentification.

---

## Lancement

1. Demarrer PostgreSQL (port 5433) avec une base `MasterAnnonce`
2. Verifier les credentials dans `persistence.xml` (myuser/mypassword)
3. `mvn clean package`
4. Deployer `target/MasterAnnonce.war` sur Tomcat 11
5. Acceder a `http://localhost:8080/MasterAnnonce/`
6. Se connecter avec **admin / admin**

## Tests

```bash
mvn test
```

Les tests utilisent H2 en memoire, aucune base PostgreSQL n'est necessaire.
