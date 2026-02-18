# MasterAnnonce - TP Dev Avance #3 - Backend API REST

**Auteur :** Fabrice CANNAN
**Formation :** BUT 3 Informatique
**Module :** R6A05 - Developpement Avance

---

## Architecture

```
com.master.air
├── dto/            DTOs avec Pattern Builder (AnnonceDTO, LoginDTO, ErrorResponse...)
├── exception/      Exceptions metier (ApiException, NotFoundException, ConflictException...)
├── model/          Entites JPA (User, Category, Annonce + @Version)
├── repository/     Couche persistence - DAO JPA (JPQL uniquement)
├── rest/           Ressources JAX-RS + ExceptionMappers + AppInitListener
├── security/       Authentification stateless (TokenStore, AuthTokenFilter)
├── service/        Couche metier - Transactions + Regles metier avancees
└── util/           JPAUtil (EntityManager factory)
```

L'architecture suit strictement le pattern en couches :
**REST Resource** -> **Service** (transactions + regles) -> **Repository** (JPQL) -> **JPA/Hibernate** -> **PostgreSQL**

Aucune logique metier dans les ressources REST. Aucun Spring.

---

## Partie I - Exposition REST

### Exercice 1 - Mise en place de JAX-RS

**Choix de configuration : Jersey (reference JAX-RS)**

Jersey a ete choisi car :
- C'est l'implementation de reference de JAX-RS (Jakarta RESTful Web Services)
- S'integre nativement avec Tomcat via le ServletContainer
- Supporte Jackson pour la serialisation JSON automatique
- Supporte Bean Validation via jersey-bean-validation
- Compatible avec les ContainerRequestFilter pour la securite
- Ne necessite pas Spring (contrainte du TP)

Configuration dans `web.xml` : le servlet Jersey scanne automatiquement les packages `com.master.air.rest` et `com.master.air.security` pour decouvrir les `@Path` et `@Provider`.

Point d'entree : `RestApplication` avec `@ApplicationPath("/api")`

Endpoints de test :
- `GET /api/helloWorld` - test simple
- `GET /api/params?name=X&age=Y` - QueryParams
- `GET /api/params/{name}` - PathParam

### Exercice 2 - API REST Annonce

| Verbe  | URI                        | Description                        | Code succes |
|--------|----------------------------|------------------------------------|-------------|
| GET    | /api/annonces              | Liste paginee (filtres optionnels) | 200         |
| GET    | /api/annonces/{id}         | Detail d'une annonce               | 200         |
| POST   | /api/annonces              | Creation (statut DRAFT)            | 201         |
| PUT    | /api/annonces/{id}         | Mise a jour complete               | 200         |
| PATCH  | /api/annonces/{id}         | Mise a jour partielle (bonus)      | 200         |
| DELETE | /api/annonces/{id}         | Suppression                        | 204         |
| POST   | /api/annonces/{id}/publish | Publier (DRAFT -> PUBLISHED)       | 200         |
| POST   | /api/annonces/{id}/archive | Archiver (PUBLISHED -> ARCHIVED)   | 200         |

**DTOs obligatoires** : `AnnonceDTO` avec Pattern Builder pour le mapping Entity <-> DTO.
Le Builder facilite la construction des DTOs en permettant de chainer les appels :
```java
AnnonceDTO.builder().id(1L).title("Mon annonce").status("DRAFT").build();
```
Le mapping inverse (Entity -> DTO) est fait via la methode statique `AnnonceDTO.fromEntity(annonce)`.

**PATCH (bonus)** : permet de modifier uniquement certains champs. Les champs `null` dans le body ne sont pas appliques. Permet aussi de changer le statut via le champ `status`.

---

## Partie II - Validation, erreurs et robustesse

### Exercice 3 - Validation API

Bean Validation sur les DTOs (`@NotBlank`, `@Size`, `@Email`, `@NotNull`).
Un `ConstraintViolationExceptionMapper` intercepte les erreurs de validation et retourne un JSON normalise :

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Erreur de validation",
  "details": ["Le titre est obligatoire", "L'email doit etre valide"],
  "timestamp": "2025-..."
}
```

### Exercice 4 - Gestion des erreurs REST

Gestion centralisee via 3 ExceptionMappers (`@Provider`) :

| Code | Exception             | Description                    |
|------|-----------------------|--------------------------------|
| 400  | ConstraintViolation   | Validation echouee             |
| 401  | UnauthorizedException | Token absent ou invalide       |
| 403  | ForbiddenException    | Pas le droit (pas l'auteur)    |
| 404  | NotFoundException     | Ressource inexistante          |
| 409  | ConflictException     | Regle metier violee            |
| 500  | Exception (generique) | Erreur interne non interceptee |

Toutes les reponses d'erreur suivent le format `ErrorResponse` normalise.

---

## Partie III - Securite

### Exercice 5 - Authentification stateless

Endpoint `POST /api/login` qui :
1. Recoit `{ "username": "...", "password": "..." }`
2. Verifie les credentials via `UserService.authenticate()`
3. Genere un token UUID via `TokenStore.generateToken(user)`
4. Retourne `{ "token": "...", "userId": ..., "username": "..." }`

Le token est stocke en memoire (`ConcurrentHashMap`) avec une expiration d'1 heure.
Aucune session HTTP : le client renvoie le token a chaque requete.

### Exercice 6 - Filtre de securite

`AuthTokenFilter` implementant `ContainerRequestFilter` (`@Provider`, `@Priority(AUTHENTICATION)`) :
- Lit le header `Authorization: Bearer <token>`
- Si absent -> 401 Unauthorized
- Si token invalide/expire -> 401 Unauthorized
- Si valide : attache `userId` et `username` au `ContainerRequestContext`
- Endpoints publics exclus : `/api/login`, `/api/helloWorld`, `/api/params`

### Exercice 7 - Regles metier avancees

Implementees dans `AnnonceService` :

1. **Seul l'auteur peut modifier/supprimer** : comparaison `annonce.getAuthor().getId()` avec `currentUserId` -> `ForbiddenException` (403)
2. **PUBLISHED ne peut etre modifie** : verification du statut -> `ConflictException` (409)
3. **Archivage obligatoire avant suppression** : `status != ARCHIVED` -> `ConflictException` (409)
4. **Gestion concurrence** : champ `@Version` sur l'entite `Annonce` -> `OptimisticLockException` interceptee en `ConflictException` (409)

---

## Partie IV - Tests & qualite logicielle

### Exercice 8 - Tests Repository (integration)

`AnnonceRepositoryIT.java` :
- BDD H2 en mode In-Memory (`persistence.xml` de test avec `create-drop`)
- Chargement d'un jeu de donnees dans `@BeforeAll` (10 annonces, 1 user, 1 categorie)
- Tests : CRUD, pagination (page 1 et 2), recherche par mot-cle, filtrage par statut/categorie, findByIdWithRelations

### Exercice 9 - Tests API REST

- **Tests unitaires** (`AnnonceServiceTest.java`) : Mockito pour mocker les repositories, verification des regles metier (auteur, statut PUBLISHED, workflow DRAFT->PUBLISHED->ARCHIVED->DELETE)
- **Tests unitaires securite** (`TokenStoreTest.java`) : generation, validation, suppression de tokens
- Suffixe `IT` pour les tests d'integration, sans suffixe pour les tests unitaires

### Exercice 10 - Industrialisation

**Separation tests unitaires / integration** :
- `maven-surefire-plugin` execute les tests unitaires (`mvn test`) en excluant `*IT.java` et `*IntegrationTest.java`
- `maven-failsafe-plugin` execute les tests d'integration (`mvn verify`) en incluant uniquement `*IT.java`
- **Interet** : les tests unitaires sont rapides (pas de BDD, pas de serveur) et s'executent a chaque build. Les tests d'integration sont plus lents (demarrent H2, chargent les donnees) et ne s'executent qu'en phase de verification. Cela permet un feedback rapide pendant le developpement tout en garantissant l'integration avant le deploiement.

**Logging structure** : logback.xml avec pattern `timestamp [thread] level logger - message`. Logs console + fichier rotatif.

**Documentation API** : la collection Postman sert de documentation executable (18 requetes couvrant tous les cas : CRUD, auth, erreurs, filtres).

---

## Problemes rencontres et solutions

### 1. Jersey ne decouvre pas les ExceptionMappers
**Probleme** : les `@Provider` imbriques dans une classe statique (`ExceptionMappers$ApiExceptionMapper`) n'etaient pas detectes par l'auto-scan Jersey.
**Solution** : declaration explicite dans `web.xml` via `jersey.config.server.provider.classnames` pour les mapper imbriques.

### 2. ContainerRequestContext.getProperty() retourne null
**Probleme** : dans la ressource REST, `ctx.getProperty("userId")` retournait null car le filtre n'attachait pas correctement l'identite.
**Solution** : verification que le filtre `AuthTokenFilter` utilise bien `ctx.setProperty()` et que l'annotation `@Priority(Priorities.AUTHENTICATION)` garantit son execution avant les ressources.

### 3. Serialisation JSON des entites JPA avec relations LAZY
**Probleme** : Jackson essayait de serialiser les proxies Hibernate des relations lazy, causant des erreurs.
**Solution** : utilisation systematique de DTOs (`AnnonceDTO.fromEntity()`) qui extraient les donnees avant la fermeture de l'EntityManager. Jamais d'entite JPA exposee directement.

### 4. OptimisticLockException avec @Version
**Probleme** : les updates concurrents causaient une `OptimisticLockException` non interceptee, retournant un 500.
**Solution** : catch explicite dans `AnnonceService.updateAnnonce()` transformant l'exception en `ConflictException` (409) avec un message explicatif.

### 5. Bean Validation sur les DTOs vs validation manuelle
**Probleme** : la validation Bean (`@Valid`) lancait une `ConstraintViolationException` non formatee en JSON.
**Solution** : `ValidationExceptionMapper` dedie qui collecte toutes les violations et les retourne dans le format `ErrorResponse` normalise.

### 6. Suppression sans archivage prealable
**Probleme** : un utilisateur pouvait supprimer directement une annonce DRAFT ou PUBLISHED.
**Solution** : verification explicite du statut dans `deleteAnnonce()` : seules les annonces ARCHIVED peuvent etre supprimees, sinon ConflictException (409).

### 7. PATCH vs PUT semantique
**Probleme** : differencier une mise a jour partielle (PATCH) d'une mise a jour complete (PUT).
**Solution** : pour PUT, tous les champs sont obligatoires (validation @Valid). Pour PATCH, seuls les champs non-null du body sont appliques, les autres conservent leur valeur actuelle.

---

## Technologies

| Technologie | Version | Role |
|---|---|---|
| Java | 17 | Langage |
| Jakarta EE | 6.0 | Servlet |
| Jersey | 3.1.5 | JAX-RS (API REST) |
| Jackson | 2.17 | Serialisation JSON |
| Hibernate ORM | 6.4.4 | JPA / ORM |
| Hibernate Validator | 8.0.1 | Bean Validation |
| PostgreSQL | 42.7.3 | Base de donnees |
| Logback | 1.4.14 | Logging structure |
| JUnit 5 | 5.10.2 | Tests |
| Mockito | 5.11.0 | Mocks |
| H2 | 2.2.224 | BDD tests |
| Maven | 3.x | Build |

---

## Lancement

```bash
# 1. Demarrer PostgreSQL (port 5433) avec base MasterAnnonce
# 2. Verifier persistence.xml (myuser/mypassword)
# 3. Build
mvn clean package

# 4. Deployer target/MasterAnnonce.war sur Tomcat 11
# 5. Tester
curl http://localhost:8080/MasterAnnonce/api/helloWorld
```

## Tests

```bash
# Tests unitaires uniquement (rapide)
mvn test

# Tests unitaires + integration
mvn verify
```

## Compte par defaut

Login : `admin` / `admin`
