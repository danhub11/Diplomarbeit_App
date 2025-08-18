# CONTRIBUTING

Danke fürs Mithelfen am Projekt! Bitte beachte für eine reibungslose Zusammenarbeit:

## Branch-Namen
- `feature/<kurz-und-klar>` für neue Features
- `fix/<ticket-oder-bug>` für Bugfixes
- `chore/<aufgabe>` für Wartung/Build/Docs

Beispiele:  
`feature/login-endpoint`, `fix/participant-nullref`, `chore/update-readme`

## Commits
- Schreibe prägnante Messages im Imperativ:
  - `Add registration endpoint`
  - `Fix null reference in BrevetService`
- Kleinere, logisch getrennte Commits sind besser als ein „Monster-Commit“.

## Pull Requests
- Ziel ist **main** im Original-Repo.
- PR-Titel: kurz & verständlich: `Feature: Registration endpoint`
- Beschreibung:
  - **Was** wurde geändert?
  - **Warum** (Kurzbegründung/Issue)?
  - **Wie getestet** (lokal, Swagger, Unit Tests)?
- Prüfe vor dem PR:
  - Build erfolgreich
  - Linter/Formatter (falls vorhanden) ausgeführt
  - Self-Review durchgeführt

## Code-Stil
- Behalte vorhandenen Stil bei.
- Nutze bestehende Services/DTOs/Patterns.
- Kein Trittbrett-Refactoring – nur ändern, was das Feature betrifft.

## Synch mit upstream
- Vor neuem Branch immer:

