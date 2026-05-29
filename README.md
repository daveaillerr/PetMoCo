# 🐾 PetMoCo — Pet Booking System

A console-based Java application for managing pet service bookings.  
Supports **Grooming**, **Sitting**, and **Walking** appointments with full user authentication and database persistence.

---

## Features

| Feature              | Description                                            |
| -------------------- | ------------------------------------------------------ |
| **User Auth**        | Register and log in with password hashing (SHA-256)    |
| **Pet Management**   | Register, view, update, and remove pets                |
| **Appointments**     | Book, view, filter by pet, and cancel appointments     |
| **Service Types**    | Grooming · Sitting · Walking                           |
| **DB Persistence**   | MySQL via JDBC — all data is saved permanently         |
| **Input Validation** | Enforces correct formats for dates, times, and numbers |

---

## Folder Structure

```
PetMoCo/
├── src/
│   ├── Main.java
│   │
│   ├── models/
│   │   ├── User.java               ← User entity
│   │   ├── Pet.java                ← Pet entity
│   │   └── Appointment.java        ← Appointment entity
│   ├── services/
│   │   ├── UserService.java        ← Register & login logic + password hashing
│   │   ├── PetService.java         ← Pet business logic
│   │   └── AppointmentService.java ← Appointment business logic
│   ├── dao/
│   │   ├── UserDAO.java            ← DB queries for users
│   │   ├── PetDAO.java             ← DB queries for pets
│   │   └── AppointmentDAO.java     ← DB queries for appointments
│   ├── utils/
│   │   ├── DatabaseConfig.java     ← JDBC connection manager
│   │   ├── ConsoleHelper.java      ← Banners, dividers, feedback
│   │   └── InputValidator.java     ← Typed, validated input readers
│   ├── menus/
│   │   ├── AuthMenu.java           ← Login / Register screen
│   │   ├── MainMenu.java           ← Top-level router
│   │   ├── PetMenu.java            ← Pet management menu
│   │   └── AppointmentMenu.java    ← Appointment management menu
│   └── data/
│       └── script.sql              ← Database setup script
├── lib/
│   ├── mysql-connector-j-9.7.0.jar
│   └── dotenv-java-3.0.0.jar
├── .env                            ← DB credentials (not committed to git)
├── .gitignore
├── README.md
└── CLAUDE.md
```

---

## Technologies Used

| Technology                | Purpose                    |
| ------------------------- | -------------------------- |
| Java 17+                  | Core application language  |
| JDBC (MySQL)              | Database connectivity      |
| MySQL                     | Relational database        |
| dotenv-java               | Loading `.env` credentials |
| SHA-256 (`java.security`) | Password hashing           |
| Scanner                   | Console input              |

---

## How to Run

### 1. Prerequisites

- Java 17 or higher installed
- MySQL server running locally
- The `lib/` directory contains both JARs

### 2. Set Up the Database

Run the SQL script to create the database and tables:

```sql
-- In MySQL Workbench or CLI:
source src/data/script.sql
```

A default admin account is seeded: **username:** `admin` **password:** `admin123`

### 3. Configure `.env`

The `.env` file in the project root must contain:

```
DB_URL=jdbc:mysql://localhost:3306/petmoco_db
DB_USER=root
DB_PASSWORD=your_password_here
```

### 4. Compile

```powershell
# From the project root
javac -cp "lib/*" -d out (Get-ChildItem src -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName)
```

Or in a single command on Linux/macOS:

```bash
javac -cp "lib/*" -d out $(find src -name "*.java")
```

### 5. Run

```powershell
java -cp "out;lib/*" main.Main
```

On Linux/macOS:

```bash
java -cp "out:lib/*" main.Main
```

### 6. VSCode (Recommended)

Open the folder in VSCode with the **Extension Pack for Java** installed.  
The `.vscode/settings.json` is pre-configured — just press **Run** on `Main.java`.

---

## Console Navigation Flow

```
App Start
  └── Welcome Banner
        └── DB Connection Test
              ├── FAIL → Error message → Exit
              └── OK  → Auth Menu
                          ├── 1. Log In → [credentials] → Main Menu
                          ├── 2. Register → [new account] → Main Menu
                          └── 0. Exit
                    Main Menu (after login)
                          ├── 1. Manage Pets
                          │       ├── Register / View All / Details / Update / Remove
                          │       └── 0. Back
                          ├── 2. Manage Appointments
                          │       ├── Book / View All / By Pet / Cancel
                          │       └── 0. Back
                          └── 0. Log Out & Exit
```

---

## Important Notes

- **Passwords** are never stored in plain text — SHA-256 hashing is applied before saving to the database.
- **Deleting a pet** also deletes all of its appointments (database CASCADE).
- **Cancelling** an appointment sets its status to `CANCELLED` — it is not deleted from the database.
- The app reads DB credentials from `.env` — keep this file out of version control (already in `.gitignore`).
- Date format: `YYYY-MM-DD` · Time format: `HH:MM` (24-hour)
