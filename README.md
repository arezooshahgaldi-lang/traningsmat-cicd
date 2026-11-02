# Träningsmat – Recept-applikation

Individuell examinationsuppgift i CI/CD: en receptapplikation för träningsmat, byggd med Java Spring Boot (backend) och HTML/CSS/JavaScript (frontend), med automatiserade tester och en fullt automatiserad CI/CD-pipeline via GitHub Actions och Render.

## Om projektet

Med Träningsmat kan man skapa, visa och ta bort recept anpassade för träning. Varje recept innehåller:

- Titel
- Beskrivning
- Kategori (t.ex. Återhämtning, Styrka)
- Kalorier
- Protein (gram)
- Svårighetsgrad (t.ex. Lätt, Medel)

Applikationen skyddar även mot dubbletter – två recept kan inte sparas med samma titel.

## Teknisk stack

- **Backend:** Java 17, Spring Boot, Spring Data JPA
- **Frontend:** HTML, CSS, vanilla JavaScript (serveras som statiska filer av Spring Boot)
- **Databas:** H2 (in-memory, för lokal utveckling och alla automatiska tester), PostgreSQL (för drift på Render)
- **Tester:** JUnit 5 (enhets- och integrationstester), Mockito, Playwright (E2E-tester)
- **CI/CD:** GitHub Actions
- **Driftsättning:** Docker + Render.com

## Köra projektet lokalt

Förutsättningar: JDK 17, Maven, IntelliJ IDEA (eller valfri IDE).

1. Klona repot:
   ```
   git clone https://github.com/arezooshahgaldi-lang/traningsmat-cicd.git
   ```
2. Öppna projektet i IntelliJ IDEA.
3. Kör klassen `TraningsmatApplication`.
4. Applikationen startar på `http://localhost:8080`. Lokalt används en H2-databas i minnet (data nollställs varje gång appen startas om).
5. H2-konsolen (för att inspektera databasen manuellt) nås på `http://localhost:8080/h2-console`.

## Köra testerna

Alla tester (enhets-, integrations- och E2E-tester) körs med:

```
mvn clean verify
```

E2E-testerna använder Playwright, som styr en riktig webbläsare (Chromium). Första gången behöver Playwrights webbläsare installeras:

```
mvn test-compile
mvn exec:java -e "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=install --with-deps chromium" "-Dexec.classpathScope=test"
```

Alla tester körs mot en H2-databas i minnet, så inga externa beroenden krävs för att köra testerna – varken lokalt eller i CI/CD-pipelinen.

## Testnivåer

- **Enhetstester** – testar enskilda klasser (t.ex. `RecipeService`) isolerat, med mockade beroenden.
- **Integrationstester** – testar att controller, service och databas samverkar korrekt genom riktiga HTTP-anrop.
- **E2E-tester (Playwright)** – testar hela flödet genom en riktig webbläsare: att skapa ett recept och se det i listan, samt att ta bort ett recept.

## CI/CD-pipeline

Projektet använder ett gren-baserat arbetsflöde:

1. Varje förändring görs i en egen, liten feature-/fix-gren.
2. En Pull Request skapas mot integrationsgrenen `dev`. GitHub Actions bygger projektet och kör alla tester automatiskt på PR:en.
3. När testerna är gröna mergas PR:en in i `dev`, vilket automatiskt triggar en ny driftsättning av utvecklingsmiljön.
4. Färdiga, testade ändringar förs vidare till `main` på samma sätt (via PR, med grön pipeline som krav), vilket automatiskt driftsätter produktionsmiljön.

Workflow-filen finns i `.github/workflows/ci.yml` och innehåller tre jobb:

- `build-and-test` – bygger projektet och kör alla tester.
- `deploy-dev` – körs bara vid en merge till `dev`, och triggar Render att driftsätta utvecklingsmiljön.
- `deploy-prod` – körs bara vid en merge till `main`, och triggar Render att driftsätta produktionsmiljön.

Driftsättningen sker genom att pipelinen anropar Renders "Deploy Hooks" (unika webbadresser). Adresserna hålls hemliga genom GitHub Secrets (`RENDER_DEPLOY_HOOK_DEV` och `RENDER_DEPLOY_HOOK_PROD`) och skrivs aldrig ut i klartext.

## Driftsättning

Applikationen körs i Docker på Render, med en `Dockerfile` som bygger projektet med Maven i ett byggsteg och sedan kör den färdiga `.jar`-filen i ett minimalt runtime-steg.

Konfigurationen styrs via Spring-profiler:

- `application.properties` – standardprofil, används lokalt och i alla tester (H2).
- `application-prod.properties` – produktionsprofil, kopplar till en riktig PostgreSQL-databas via miljövariabler (`DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`). Aktiveras med miljövariabeln `SPRING_PROFILES_ACTIVE=prod`.

## Live-miljöer

- **Utveckling:** https://traningsmat-dev.onrender.com
- **Produktion:** https://traningsmat-prod.onrender.com

Observera: eftersom applikationen körs på Renders kostnadsfria nivå kan den första förfrågan efter en tids inaktivitet ta upp till ~50 sekunder, eftersom tjänsten då måste starta upp igen ("cold start").


