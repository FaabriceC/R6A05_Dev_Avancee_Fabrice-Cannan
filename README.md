# MasterAnnonce - TP Dev Avance #4 - Migration Spring Boot

**Auteur :** Fabrice CANNAN  
**Formation :** BUT 3 Informatique - R6A05 Dev Avance

---

## Architecture

```
com.master.air
├── aspect/          LoggingAspect (@Around sur services, duree, exceptions)
├── config/          SecurityConfig, DataInitializer, CorrelationIdFilter
├── controller/      AnnonceController, AuthController, MetaController
├── dto/             Records : AnnonceDTO, AnnonceCreateDTO, AnnoncePatchDTO, LoginDTO, ErrorResponse
├── exception/       NotFoundException, ConflictException, ForbiddenException, GlobalExceptionHandler
├── mapper/          MapStruct : AnnonceMapper, UserMapper, CategoryMapper
├── model/           Entites JPA : User, Category, Annonce, AnnonceStatus, Role
├── repository/      Spring Data JPA : AnnonceRepository, UserRepository, CategoryRepository
├── security/        JwtService, JwtAuthenticationFilter
├── service/         AnnonceService, AuthService
└── specification/   AnnonceSpecifications (JpaSpecificationExecutor)
```

**Couches :** Controller → Service (@Transactional) → Repository (Spring Data JPA) → PostgreSQL

---

## Partie I - Migration Spring Boot

### Exercice 1 - Configuration

Migration de JAX-RS/Jersey vers Spring Boot 3.4.3. Le `pom.xml` utilise `spring-boot-starter-parent` et inclut : spring-web, spring-data-jpa, spring-security, spring-aop, spring-actuator, springdoc-openapi, jjwt, mapstruct, postgresql.

Configuration via `application.yml` avec variables d'environnement (`SPRING_DATASOURCE_URL`, etc.) pour Docker/K8s.

**Ce que Spring automatise :** auto-configuration DataSource/JPA, scan des composants, gestion du lifecycle, serialisation JSON, gestion des erreurs.
**Ce qu'il masque :** creation d'EntityManagerFactory, gestion des transactions (proxy AOP), injection de dependances.

### Exercice 2 - Migration endpoints REST + MapStruct

Endpoints migres avec `@RestController`, `@GetMapping`, `@PostMapping`, `@Valid`, etc.

**MapStruct** remplace le Pattern Builder du TP3. Plus de `new DTO()` ni de mapping manuel :
- `AnnonceMapper.toDTO(entity)` : Entity → DTO (avec `@Mapping` pour category.id, author.username)
- `AnnonceMapper.toEntity(dto)` : CreateDTO → Entity (ignore id, date, status, version, relations)
- `AnnonceMapper.updateFromPatch(dto, entity)` : PATCH partiel via `@MappingTarget` + `NullValuePropertyMappingStrategy.IGNORE`

**Bonus PATCH :** met a jour uniquement les champs non-null du body.

### Exercice 3 - Spring Data JPA + Specifications + Introspection

Repositories : `JpaRepository<Annonce, Long>` + `JpaSpecificationExecutor<Annonce>`. Plus de JPQL manuel pour le CRUD.

**Specifications composables** dans `AnnonceSpecifications` :
- `hasKeyword(q)` : LIKE sur title/description
- `hasStatus(status)`, `hasCategoryId(id)`, `hasAuthorId(id)`
- `createdAfter(from)`, `createdBefore(to)`
- `fetchRelations()` : JOIN FETCH pour eviter N+1

Endpoint : `GET /api/annonces?q=xxx&status=DRAFT&categoryId=1&authorId=2&fromDate=...&toDate=...&page=0&size=10&sort=date,desc`

**Introspection (Exercice 3.4)** : `GET /api/meta/annonces` utilise `Annonce.class.getDeclaredFields()` pour generer dynamiquement la liste des champs filtrables, triables, et cherchables (detection automatique des champs String).

---

## Partie II - Securite JWT

### Exercice 4 - Authentification JWT

`POST /api/auth/login` → token JWT signe (HMAC SHA-256) contenant : userId, username (subject), role, expiration (1h).

Librairie : `io.jsonwebtoken` (jjwt 0.12.6). Secret configurable via `app.jwt.secret`.

### Exercice 5 - Spring Security

`SecurityFilterChain` stateless (pas de session HTTP). `JwtAuthenticationFilter extends OncePerRequestFilter` extrait le token du header `Authorization: Bearer xxx`, valide le JWT et peuple le `SecurityContext`.

Endpoints publics : `/api/auth/**`, `/swagger-ui/**`, `/actuator/**`, `/api/meta/**`.

### Exercice 6 - Regles metier + AOP

**Regles metier** dans `AnnonceService` :
1. Seul l'auteur peut modifier/supprimer → `ForbiddenException` (403)
2. Seul un ADMIN peut archiver → `@PreAuthorize("hasRole('ADMIN')")` (403)
3. PUBLISHED non modifiable → `ConflictException` (409)
4. Archivage obligatoire avant suppression → `ConflictException` (409)
5. `@Version` pour concurrence optimiste → `ConflictException` (409)

**Logging AOP** : `LoggingAspect` avec `@Around("execution(* com.master.air.service..*(..))")`. Log entree/sortie, duree, exceptions. Pas de log d'objets lazy ni de secrets.

**Correlation ID** : `CorrelationIdFilter` genere un UUID par requete, propage via `MDC` dans les logs.

---

## Partie III - Tests

### Exercice 7 - Tests unitaires Service (Mockito)

`AnnonceServiceTest` : 6 tests verifiant les regles metier (auteur, statut PUBLISHED, archivage, NotFoundException).

### Exercice 8 - Tests d'integration REST (MockMvc)

`AnnonceControllerIT` : 9 tests couvrant login, auth 403, token invalide, CRUD complet, role insuffisant, validation 400, Swagger, Actuator, Meta.

### Exercice 9 - Base de test

H2 en memoire via `application-test.yml` (`@ActiveProfiles("test")`). Tests isoles : `create-drop` a chaque run.

---

## Partie IV - Documentation & Observabilite

### Exercice 10 - OpenAPI / Swagger

SpringDoc OpenAPI integre. Swagger UI : `http://localhost:8080/swagger-ui/index.html`. Annotations `@Tag`, `@Operation` sur chaque controller.

### Exercice 11 - Actuator

- `/actuator/health` : health check avec details (PostgreSQL)
- `/actuator/info` : infos applicatives
- Probes Kubernetes : `readiness` et `liveness` actives

---

## Partie V - Industrialisation

### Exercice 12 - Docker

**Dockerfile multi-stage** : build Maven + JRE Alpine. **docker-compose** : app + PostgreSQL avec healthcheck.

```bash
docker-compose up --build
```

### Exercice 13 - CI GitHub Actions

**Choix DB en CI : PostgreSQL via service container** (Option 2). Justification : simple, explicite, pas de dependance a Testcontainers (qui necessite Docker-in-Docker). Le service PostgreSQL est declare dans le workflow avec healthcheck.

Pipeline :
1. Checkout
2. Setup Java (matrice 17 + 21) + cache Maven
3. `mvn -B clean verify` (tests unitaires + integration)
4. Upload artifact `master-annonce-jar`
5. Upload JaCoCo report
6. Build Docker (sur main uniquement)

**Artifact produit** : `master-annonce-jar`

---

## SuperBonus - Kubernetes (Minikube)

Manifests dans `/k8s/` :
- `postgres-secret.yaml` : credentials base64 (PAS en clair)
- `app-configmap.yaml` : SPRING_DATASOURCE_URL, SPRING_PROFILES_ACTIVE
- `postgres-deployment.yaml` + `postgres-service.yaml`
- `app-deployment.yaml` (replicas: 2, readinessProbe, livenessProbe) + `app-service.yaml` (NodePort 30080)
- `ingress.yaml`

```bash
minikube start
minikube addons enable ingress
eval $(minikube docker-env)
docker build -t masterannonce:1.0 .
kubectl apply -f k8s/
kubectl get pods
minikube service masterannonce-service --url
```

---

## Problemes rencontres et solutions

### 1. MapStruct ne genere pas les implementations
**Probleme :** classes `*MapperImpl` absentes au compile.
**Solution :** ajouter `mapstruct-processor` dans `annotationProcessorPaths` du `maven-compiler-plugin`.

### 2. Serialisation JSON des entities JPA Lazy
**Probleme :** Jackson tente de serialiser les proxies Hibernate → erreur.
**Solution :** DTOs obligatoires. MapStruct accede aux champs dans la transaction, le DTO (record) est ensuite serialise sans proxy.

### 3. Spring Security bloque tout par defaut
**Probleme :** 403 sur tous les endpoints apres ajout de spring-boot-starter-security.
**Solution :** `SecurityFilterChain` explicite avec `authorizeHttpRequests()` pour differencier endpoints publics/proteges.

### 4. @PreAuthorize ne fonctionne pas
**Probleme :** `@PreAuthorize("hasRole('ADMIN')")` ignore silencieusement.
**Solution :** ajouter `@EnableMethodSecurity` sur la classe `SecurityConfig`.

### 5. Pagination Spring Data vs ancien PaginatedResult
**Probleme :** Spring Data retourne `Page<T>`, pas notre ancien `PaginatedResult<T>`.
**Solution :** utiliser directement `Page<AnnonceDTO>` retourne par `annonceRepo.findAll(spec, pageable).map(mapper::toDTO)`. Spring serialise automatiquement avec metadata pagination.

### 6. JOIN FETCH + Pagination = warning Hibernate
**Probleme :** `HHH90003004: firstResult/maxResults specified with collection fetch`.
**Solution :** dans `AnnonceSpecifications.fetchRelations()`, verifier `query.getResultType() != Long.class` pour eviter le fetch sur les count queries.

### 7. Correlation ID perdu entre requetes
**Probleme :** MDC pas propage dans les threads async.
**Solution :** `CorrelationIdFilter` avec `MDC.put()` dans un try/finally pour garantir le cleanup.

### 8. H2 vs PostgreSQL dialecte en test
**Probleme :** certaines fonctions PostgreSQL absentes en H2.
**Solution :** `application-test.yml` separe avec `H2Dialect` et `create-drop`.

---

## Comptes par defaut

| Username | Password | Role       |
|----------|----------|------------|
| admin    | admin    | ROLE_ADMIN |
| user     | user     | ROLE_USER  |

## Commandes

```bash
# Dev local
mvn spring-boot:run

# Tests unitaires
mvn test

# Tests unitaires + integration
mvn verify

# Docker
docker-compose up --build

# Swagger
http://localhost:8080/swagger-ui/index.html
```
