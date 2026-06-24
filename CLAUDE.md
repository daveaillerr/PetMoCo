# CLAUDE.md — PetMoCo AI Context File

This document is written for AI assistants to quickly understand the PetMoCo codebase and continue development efficiently.

---

## Project Summary

**PetMoCo** is a console-based Java pet booking system. Users can register pets and book service appointments (Grooming, Sitting, Walking). It uses MySQL for persistence, reads credentials from a `.env` file, and is organized into a layered architecture.

**Language:** Java 17+  
**Database:** MySQL 8+  
**Key Libraries:** `mysql-connector-j-9.7.0.jar`, `dotenv-java-3.0.0.jar`  
**No framework** — pure Java with JDBC.

---

## Architecture Overview

```
main/Main.java
    │
    ├── utils/DatabaseConfig.java     (DB connection — singleton pattern)
    ├── menus/AuthMenu.java           (login / register — uses UserService)
    │       └── services/UserService.java → dao/UserDAO.java → MySQL
    │
    └── menus/MainMenu.java           (top-level router after login)
            ├── menus/PetMenu.java        → services/PetService.java → dao/PetDAO.java
            └── menus/AppointmentMenu.java → services/AppointmentService.java → dao/AppointmentDAO.java
```

**Layer responsibilities:**
- `main/` — entry point only; no business logic
- `menus/` — user interaction (Scanner I/O); delegates all logic to services
- `services/` — validation + business rules; delegates DB operations to DAOs
- `dao/` — raw JDBC SQL queries; no business logic
- `models/` — plain Java objects (POJOs) matching DB tables
- `utils/` — cross-cutting concerns (DB config, console helpers, input parsing)
- `data/` — SQL schema script

---

## Package Reference

### `main`
| File | Purpose |
|---|---|
| `Main.java` | Bootstraps the app: prints banner → tests DB → auth → main menu → exit |

### `models`
| File | DB Table | Key Fields |
|---|---|---|
| `User.java` | `users` | `userId`, `username`, `password` (SHA-256), `role` (CUSTOMER/ADMIN) |
| `Pet.java` | `pets` | `petId`, `petName`, `petType`, `breed`, `age`, `ownerName`, `ownerContact` |
| `Appointment.java` | `appointments` | `appointmentId`, `petId`, `petName` (joined), `appointmentDate`, `appointmentTime`, `serviceType`, `status`, `notes` |

All models have standard getters/setters and a `toString()` for console display.

### `dao`
| File | Responsibility |
|---|---|
| `UserDAO.java` | `findByUsername()`, `insert()`, `usernameExists()` |
| `PetDAO.java` | `insert()`, `findAll()`, `findById()`, `update()`, `delete()` |
| `AppointmentDAO.java` | `insert()`, `findAll()`, `findById()`, `findByPetId()`, `cancel()` |

All DAOs use `DatabaseConfig.getConnection()` and `PreparedStatement` to prevent SQL injection.  
`AppointmentDAO` JOINs `pets` to fetch `pet_name` for display.

### `services`
| File | Key Logic |
|---|---|
| `UserService.java` | `register()` checks uniqueness then hashes password; `login()` hashes + compares |
| `PetService.java` | Validates age (0–50), non-empty name/type before inserting |
| `AppointmentService.java` | Validates pet exists + service type is in `VALID_SERVICES` list; `cancelAppointment()` rejects already-cancelled |

`AppointmentService.VALID_SERVICES = ["GROOMING", "SITTING", "WALKING"]`

### `utils`
| File | Purpose |
|---|---|
| `DatabaseConfig.java` | Singleton JDBC connection; reads `DB_URL`, `DB_USER`, `DB_PASSWORD` from `.env` via dotenv-java |
| `ConsoleHelper.java` | `printBanner()`, `printHeader(title)`, `printDivider()`, `printSuccess/Error/Info(msg)`, `pause(scanner)` |
| `InputValidator.java` | `readNonEmptyString()`, `readOptionalString()`, `readInt(min,max)`, `readPositiveInt()`, `readDate()` (YYYY-MM-DD), `readTime()` (HH:MM), `readServiceType()`, `readPassword()` |

`readPassword()` uses `System.console().readPassword()` when available (terminal), falls back to plain Scanner for IDE environments.

### `menus`
| File | Shows |
|---|---|
| `AuthMenu.java` | Login / Register / Exit. Returns `User` on success, `null` on exit |
| `MainMenu.java` | Manage Pets / Manage Appointments / Log Out. Receives `User currentUser` |
| `PetMenu.java` | Register / View All / Details / Update / Remove |
| `AppointmentMenu.java` | Book / View All / By Pet / Cancel |

All menus loop until the user picks `0` (back/exit).

### `data`
| File | Purpose |
|---|---|
| `script.sql` | Creates `petmoco_db`, `users`, `pets`, `appointments` tables; seeds default admin |

---

## Database Schema

```sql
users        (user_id PK, username UNIQUE, password VARCHAR(64), role, created_at)
pets         (pet_id PK, pet_name, pet_type, breed, age, owner_name, owner_contact, created_at)
appointments (appointment_id PK, pet_id FK→pets, appointment_date, appointment_time,
              service_type, status DEFAULT 'SCHEDULED', notes, created_at)
```

`appointments.pet_id` has `ON DELETE CASCADE` — deleting a pet removes all its appointments.

---

## Coding Conventions

- **Package names:** all lowercase (`main`, `models`, `dao`, `services`, `utils`, `menus`)
- **Class names:** PascalCase
- **Methods:** camelCase; named after actions (`findById`, `registerPet`, `cancelAppointment`)
- **DAO methods:** descriptive SQL verbs (`insert`, `findAll`, `findById`, `update`, `delete`, `cancel`)
- **No abbreviations** in public API names
- **Comments:** only on non-obvious logic; avoid restating what the code does
- All user-visible strings go through `ConsoleHelper` (never raw `System.out.println` for status/error)
- Business rules belong in `services/`, never in `menus/` or `dao/`

---

## Business Logic Notes

### Password Handling
- Plain text is **never** stored — `UserService.hash()` applies SHA-256 before any DB write
- Comparison: `storedHash.equals(hash(inputPassword))`
- Default admin: username=`admin`, password=`admin123` (seeded in `script.sql`)

### Service Types
- Constrained to `["GROOMING", "SITTING", "WALKING"]` in `AppointmentService.VALID_SERVICES`
- `InputValidator.readServiceType()` presents a numbered list — no freeform input

### Appointment Cancellation
- Sets `status = 'CANCELLED'` — does **not** delete the row
- Already-cancelled appointments cannot be cancelled again (checked in `AppointmentService`)

### Pet Deletion
- Asks for `YES` confirmation in `PetMenu`
- Database cascade handles appointment cleanup automatically

---

## Input/Output Flow

```
User presses key
    ↓
InputValidator.readXxx(scanner, prompt)  ← loops until valid
    ↓
Menu method receives value
    ↓
Service.doSomething(params)              ← validates business rules
    ↓
DAO.insert/find/update(params)           ← executes PreparedStatement
    ↓
Returns model or boolean to service
    ↓
Service returns model or null to menu
    ↓
ConsoleHelper.printSuccess/Error(msg)   ← displays result
    ↓
ConsoleHelper.pause(scanner)            ← waits for Enter
```

---

## Environment & Configuration

- Credentials in `.env` (project root):
  ```
  DB_URL=jdbc:mysql://localhost:3306/petmoco_db
  DB_USER=root
  DB_PASSWORD=...
  ```
- `DatabaseConfig` uses `Dotenv.configure().ignoreIfMissing()` — if `.env` is absent, variables fall back to `null` and connection fails gracefully with a helpful error message.

---

## Compile & Run Commands

```powershell
# Compile (PowerShell, from project root)
javac -encoding UTF-8 -cp "lib/*" -d out (Get-ChildItem src -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName)

# Run
java -cp "out;lib/*" main.Main
```

```bash
# Linux/macOS
javac -cp "lib/*" -d out $(find src -name "*.java")
java -cp "out:lib/*" main.Main
```

---

## Refactor Notes

This project was converted from a Swing/JFrame GUI to a console application.

**What was removed:**
- `src/system/page/LandingPage.java` — 402-line Swing panel with custom painting
- All `JFrame`, `JPanel`, `SwingUtilities`, `CountDownLatch`, Swing imports in `Main.java`
- `UIManager.setLookAndFeel()` setup

**What was kept (logic unchanged):**
- `DatabaseConfig.java` — same JDBC logic, moved to `utils` package
- `script.sql` — schema preserved, `users` table added

**What was added:**
- Full auth system (UserDAO, UserService, AuthMenu)
- Two new models: `Pet.java`, `Appointment.java`
- Complete CRUD DAO and service layers
- All console menus (MainMenu, PetMenu, AppointmentMenu)
- Utility classes (ConsoleHelper, InputValidator)

---

## Known Limitations

1. **No role-based access control in menus** — all logged-in users see the same options. The `role` field (CUSTOMER/ADMIN) is stored but not enforced in menus yet.
2. **No appointment editing** — only booking and cancellation are supported. Rescheduling requires cancel + rebook.
3. **Password recovery** — not implemented. If a user forgets their password, they need DB-level reset.
4. **No pagination** — `View All` lists load all rows. May be slow with very large datasets.
5. **Single-connection pooling** — `DatabaseConfig` holds one static connection. Not suitable for multi-threaded use.

---

## Future Improvement Suggestions

| Priority | Feature |
|---|---|
| High | Role enforcement: admin can view all users/pets; customer sees only their own |
| High | Reschedule appointment (update date/time) |
| Medium | Search/filter pets by name or type |
| Medium | Search/filter appointments by date range or service type |
| Medium | Connection pooling (HikariCP) for multi-threaded readiness |
| Low | Export appointment list to CSV |
| Low | Email/SMS reminder notifications |
| Low | Admin panel: manage all users, reset passwords |
