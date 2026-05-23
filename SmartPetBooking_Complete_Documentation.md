# Smart Pet Booking System
## Complete Project Planning & Documentation Guide

### Object-Oriented Programming with Database Integration
**Final Project Documentation**

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [System Overview](#system-overview)
3. [System Architecture](#system-architecture)
4. [Database Design](#database-design)
5. [Class Design & OOP Concepts](#class-design-oop-concepts)
6. [2-Week Sprint Plan](#2-week-sprint-plan)
7. [Feature Specifications](#feature-specifications)
8. [User Interface Design](#user-interface-design)
9. [Testing Strategy](#testing-strategy)
10. [Report Generation](#report-generation)
11. [Defense Preparation](#defense-preparation)
12. [Code Examples & Best Practices](#code-examples-best-practices)

---

## 1. Executive Summary

### Project Title
**Smart Pet Booking System** - A centralized appointment management system for pet care services

### Problem Statement
Many small pet care businesses in the Philippines rely on:
- Handwritten logs and manual record-keeping
- Walk-in appointments without scheduling
- Facebook Messenger or chat-based reservations
- Inconsistent client information management

**These lead to:**
- Scheduling conflicts and double bookings
- Lost appointment records
- Duplicate client entries
- Inefficient staff assignment
- Poor service history tracking

### Solution
A Java-based desktop application with MySQL database that provides:
- **For Pet Owners**: Online booking, pet profile management, appointment history
- **For Employees**: Schedule viewing, appointment management, client information access
- **For Admins**: Complete system oversight, staff management, reports generation

### Technology Stack
- **Language**: Java (JDK 8+)
- **Database**: MySQL 8.0
- **Interface**: Scanner (Console) + JOptionPane (GUI dialogs)
- **JDBC**: MySQL Connector/J
- **IDE**: NetBeans / Eclipse / IntelliJ IDEA

### Project Scope
- **Timeline**: 2 weeks (14 days)
- **Team Size**: 5 members
  - 3 Backend developers (Database, Logic, CRUD)
  - 2 UI/UX developers (Interface, User Experience)
- **Deliverables**: Working application + Documentation + Presentation

---

## 2. System Overview

### Core Features (10 Required)

1. **Login Module**
   - Username/password authentication
   - Role-based access (Admin, Employee, Pet Owner)
   - Session management
   - Password validation

2. **Main Menu**
   - Role-specific menu options
   - Navigation between features
   - Modern, clean interface
   - Help/instructions

3. **Add Record**
   - Add pet owners
   - Register pets
   - Create appointments
   - Add services
   - Register employees

4. **View Records**
   - List all appointments
   - View pet profiles
   - Display owner information
   - Show service catalog
   - Employee directory

5. **Search Record**
   - Search by owner name
   - Find by pet name
   - Search by date
   - Filter by status
   - Multiple search criteria

6. **Update Record**
   - Modify appointment details
   - Update pet information
   - Edit owner profiles
   - Change appointment status
   - Assign staff to appointments

7. **Delete Record**
   - Cancel appointments
   - Remove pets (with confirmation)
   - Deactivate users
   - Archive old records
   - Soft delete implementation

8. **Report Generation**
   - Daily appointment report
   - Monthly revenue report
   - Customer activity report
   - Employee performance report
   - Service popularity report

9. **Input Validation**
   - Email format validation
   - Phone number validation
   - Date/time validation
   - Required field checking
   - Data type validation

10. **Exception Handling**
    - Database connection errors
    - Invalid input handling
    - Record not found errors
    - Duplicate entry prevention
    - Transaction rollback

---

## 3. System Architecture

### Three-Tier Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  (UI - User Interface)                                       │
│  - LoginUI.java                                              │
│  - MenuManager.java                                          │
│  - AdminUI.java, PetOwnerUI.java, EmployeeUI.java          │
│  - UIHelper.java (formatting utilities)                      │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                    BUSINESS LOGIC LAYER                      │
│  (Services - Business Rules)                                 │
│  - AuthenticationService.java                                │
│  - AppointmentService.java (booking validation)              │
│  - PaymentService.java                                       │
│  - ReportService.java                                        │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                    DATA ACCESS LAYER                         │
│  (DAO - Database Operations)                                 │
│  - BaseDAO.java (abstract)                                   │
│  - UserDAO, PetOwnerDAO, PetDAO                             │
│  - AppointmentDAO, ServiceDAO, PaymentDAO                   │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE LAYER                            │
│  MySQL Database: smart_pet_booking                           │
│  Tables: users, pet_owners, employees, pets,                │
│          services, appointments, payments                    │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure

```
src/
├── main/
│   └── Main.java (Entry point)
├── config/
│   └── DatabaseConfig.java (Singleton pattern)
├── models/
│   ├── User.java (Base class)
│   ├── PetOwner.java (Extends User)
│   ├── Employee.java (Extends User)
│   ├── Pet.java
│   ├── Service.java
│   ├── Appointment.java
│   └── Payment.java
├── dao/
│   ├── BaseDAO.java (Abstract)
│   ├── UserDAO.java
│   ├── PetOwnerDAO.java
│   ├── PetDAO.java
│   ├── AppointmentDAO.java
│   ├── ServiceDAO.java
│   └── PaymentDAO.java
├── services/
│   ├── AuthenticationService.java
│   ├── AppointmentService.java
│   ├── PaymentService.java
│   └── ReportService.java
├── ui/
│   ├── LoginUI.java
│   ├── MenuManager.java
│   ├── AdminUI.java
│   ├── PetOwnerUI.java
│   ├── EmployeeUI.java
│   └── UIHelper.java
├── utils/
│   ├── InputValidator.java
│   ├── DateTimeUtil.java
│   ├── PasswordUtil.java
│   └── ConsoleColors.java
├── exceptions/
│   ├── DatabaseException.java
│   ├── ValidationException.java
│   ├── AuthenticationException.java
│   ├── RecordNotFoundException.java
│   └── DuplicateRecordException.java
└── reports/
    ├── Report.java (Abstract)
    ├── AppointmentReport.java
    ├── RevenueReport.java
    └── CustomerReport.java
```

### Design Patterns Used

1. **Singleton Pattern**: DatabaseConfig (single connection instance)
2. **Data Access Object (DAO)**: Separates database operations
3. **Template Method**: BaseDAO provides common operations
4. **Factory Pattern**: Report generation
5. **MVC Pattern**: Separation of UI, Logic, and Data

---

## 4. Database Design

### Entity Relationship Diagram (ERD) Explanation

**Entities and Relationships:**

1. **users** (1) ←→ (1) **pet_owners** 
   - One-to-One relationship
   - A user can be a pet owner
   - Inheritance in database

2. **users** (1) ←→ (1) **employees**
   - One-to-One relationship
   - A user can be an employee
   - Inheritance in database

3. **pet_owners** (1) ←→ (Many) **pets**
   - One-to-Many relationship
   - One owner can have multiple pets
   - Foreign key: pets.owner_id → pet_owners.owner_id

4. **pet_owners** (1) ←→ (Many) **appointments**
   - One-to-Many relationship
   - One owner can make multiple appointments
   - Foreign key: appointments.owner_id → pet_owners.owner_id

5. **pets** (1) ←→ (Many) **appointments**
   - One-to-Many relationship
   - One pet can have multiple appointments
   - Foreign key: appointments.pet_id → pets.pet_id

6. **services** (1) ←→ (Many) **appointments**
   - One-to-Many relationship
   - One service can be booked many times
   - Foreign key: appointments.service_id → services.service_id

7. **employees** (1) ←→ (Many) **appointments**
   - One-to-Many relationship (optional)
   - One employee handles many appointments
   - Foreign key: appointments.employee_id → employees.employee_id (nullable)

8. **appointments** (1) ←→ (1) **payments**
   - One-to-One relationship
   - Each appointment has one payment
   - Foreign key: payments.appointment_id → appointments.appointment_id

### Database Tables (7 Tables - Requirement Met ✅)

#### Table 1: users
```
Purpose: Base table for authentication and user management
Fields:
- user_id (PK, INT, AUTO_INCREMENT)
- username (VARCHAR, UNIQUE, NOT NULL)
- password (VARCHAR, NOT NULL)
- user_type (ENUM: ADMIN, EMPLOYEE, PET_OWNER)
- email (VARCHAR, UNIQUE, NOT NULL)
- phone (VARCHAR)
- created_at (TIMESTAMP)
- last_login (TIMESTAMP)
- is_active (BOOLEAN)

Indexes:
- PRIMARY KEY (user_id)
- UNIQUE (username, email)
- INDEX (user_type)
```

#### Table 2: pet_owners
```
Purpose: Store pet owner information
Fields:
- owner_id (PK, INT, AUTO_INCREMENT)
- user_id (FK, INT, UNIQUE, NOT NULL)
- first_name (VARCHAR, NOT NULL)
- last_name (VARCHAR, NOT NULL)
- address (TEXT)
- city (VARCHAR)
- emergency_contact (VARCHAR)

Relationships:
- FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
```

#### Table 3: employees
```
Purpose: Store employee information
Fields:
- employee_id (PK, INT, AUTO_INCREMENT)
- user_id (FK, INT, UNIQUE, NOT NULL)
- first_name (VARCHAR, NOT NULL)
- last_name (VARCHAR, NOT NULL)
- position (VARCHAR, NOT NULL)
- specialization (VARCHAR)
- hire_date (DATE, NOT NULL)
- hourly_rate (DECIMAL)

Relationships:
- FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
```

#### Table 4: pets
```
Purpose: Store registered pet information
Fields:
- pet_id (PK, INT, AUTO_INCREMENT)
- owner_id (FK, INT, NOT NULL)
- pet_name (VARCHAR, NOT NULL)
- species (ENUM: DOG, CAT, BIRD, RABBIT, OTHER)
- breed (VARCHAR)
- age (INT)
- weight (DECIMAL)
- gender (ENUM: MALE, FEMALE)
- medical_notes (TEXT)
- special_requirements (TEXT)
- registration_date (TIMESTAMP)

Relationships:
- FOREIGN KEY (owner_id) REFERENCES pet_owners(owner_id) ON DELETE CASCADE
- INDEX (owner_id)
```

#### Table 5: services
```
Purpose: Catalog of available pet care services
Fields:
- service_id (PK, INT, AUTO_INCREMENT)
- service_name (VARCHAR, NOT NULL)
- service_type (ENUM: GROOMING, PET_SITTING, PET_WALKING, VETERINARY, TRAINING)
- description (TEXT)
- base_price (DECIMAL, NOT NULL)
- duration_minutes (INT, NOT NULL)
- is_available (BOOLEAN)
- created_at (TIMESTAMP)
```

#### Table 6: appointments
```
Purpose: Store all appointment bookings
Fields:
- appointment_id (PK, INT, AUTO_INCREMENT)
- pet_id (FK, INT, NOT NULL)
- owner_id (FK, INT, NOT NULL)
- service_id (FK, INT, NOT NULL)
- employee_id (FK, INT, NULLABLE)
- appointment_date (DATE, NOT NULL)
- appointment_time (TIME, NOT NULL)
- status (ENUM: PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED)
- total_price (DECIMAL, NOT NULL)
- notes (TEXT)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

Relationships:
- FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
- FOREIGN KEY (owner_id) REFERENCES pet_owners(owner_id) ON DELETE CASCADE
- FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE RESTRICT
- FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE SET NULL

Indexes:
- INDEX (appointment_date)
- INDEX (status)
```

#### Table 7: payments
```
Purpose: Track payment transactions
Fields:
- payment_id (PK, INT, AUTO_INCREMENT)
- appointment_id (FK, INT, NOT NULL)
- payment_date (TIMESTAMP)
- amount (DECIMAL, NOT NULL)
- payment_method (ENUM: CASH, CREDIT_CARD, DEBIT_CARD, GCASH, PAYMAYA)
- payment_status (ENUM: PENDING, COMPLETED, REFUNDED)
- transaction_reference (VARCHAR)

Relationships:
- FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE CASCADE
```

### Database Normalization

**1st Normal Form (1NF)**: ✅
- All tables have atomic values
- No repeating groups
- Each field contains single value

**2nd Normal Form (2NF)**: ✅
- All non-key attributes fully dependent on primary key
- No partial dependencies

**3rd Normal Form (3NF)**: ✅
- No transitive dependencies
- All non-key attributes depend only on primary key

**Example:**
- Pet owner information in separate table (not in appointments)
- Service details in services table (not duplicated in appointments)
- User authentication separate from profile details

---

## 5. Class Design & OOP Concepts

### Required OOP Concepts Mapping

#### 1. Classes and Objects (✅ 8+ Classes)

**Model Classes:**
1. User.java - Base user entity
2. PetOwner.java - Pet owner entity
3. Employee.java - Employee entity
4. Pet.java - Pet entity
5. Service.java - Service entity
6. Appointment.java - Appointment entity
7. Payment.java - Payment entity
8. AppointmentStatus.java - Enum class

**How it demonstrates:**
- Each class represents a real-world entity
- Objects are instances of these classes
- Example: `PetOwner owner = new PetOwner("Juan", "Cruz", ...);`

#### 2. Encapsulation (✅ All Model Classes)

**Implementation:**
```
User.java:
- private int userId;
- private String username;
- private String password;

+ public int getUserId() { return userId; }
+ public void setUserId(int userId) { this.userId = userId; }
```

**How it demonstrates:**
- All fields are private
- Access through public getters/setters
- Data hiding and protection
- Internal implementation hidden from outside

#### 3. Constructors (✅ All Model Classes)

**Types Used:**
1. **Default Constructor**: `public User() { ... }`
2. **Parameterized Constructor**: `public User(String username, String password) { ... }`
3. **Full Constructor**: For database retrieval with all fields

**Example:**
```java
// Default
public PetOwner() {
    super();
    this.pets = new ArrayList<>();
}

// Parameterized
public PetOwner(String firstName, String lastName, ...) {
    this.firstName = firstName;
    this.lastName = lastName;
}
```

#### 4. Inheritance (✅ Multiple Examples)

**Implementation:**

**Example 1: User Hierarchy**
```
User (Base Class)
├── PetOwner (Child Class)
└── Employee (Child Class)
```

**Code Structure:**
```java
public class User {
    protected int userId;
    protected String username;
    // Base functionality
}

public class PetOwner extends User {
    private String firstName;
    private String lastName;
    // Additional pet owner specific fields
}
```

**Example 2: DAO Hierarchy**
```
BaseDAO<T> (Abstract Base)
├── UserDAO
├── PetOwnerDAO
├── AppointmentDAO
└── PaymentDAO
```

**Example 3: Report Hierarchy**
```
Report (Abstract Base)
├── AppointmentReport
├── RevenueReport
└── CustomerReport
```

**How it demonstrates:**
- Child classes inherit parent properties/methods
- Code reusability
- IS-A relationship (PetOwner IS-A User)
- extends keyword usage

#### 5. Polymorphism (✅ Multiple Forms)

**Method Overriding:**
```java
// In User class (parent)
public String getDisplayName() {
    return username;
}

// In PetOwner class (child) - OVERRIDE
@Override
public String getDisplayName() {
    return firstName + " " + lastName;
}
```

**Polymorphic Behavior:**
```java
User user1 = new PetOwner(...);  // Polymorphism
User user2 = new Employee(...);   // Polymorphism

// Same method call, different behavior
System.out.println(user1.getDisplayName());  // Shows full name
System.out.println(user2.getDisplayName());  // Shows employee name
```

**Abstract Method Implementation:**
```java
// BaseDAO abstract method
public abstract boolean insert(T entity);

// AppointmentDAO implementation
@Override
public boolean insert(Appointment appointment) {
    // Specific implementation for appointments
}
```

#### 6. Abstraction (✅ Abstract Classes)

**Example 1: BaseDAO**
```java
public abstract class BaseDAO<T> {
    // Abstract methods (must be implemented)
    public abstract boolean insert(T entity);
    public abstract boolean update(T entity);
    public abstract boolean delete(int id);
    public abstract T findById(int id);
    
    // Concrete methods (shared by all)
    protected int executeUpdate(String sql, Object... params) {
        // Common implementation
    }
}
```

**Example 2: Report**
```java
public abstract class Report {
    protected String title;
    protected LocalDate generatedDate;
    
    // Abstract method - each report implements differently
    public abstract String generateReport();
    
    // Concrete method - common to all reports
    public void printReport() {
        System.out.println(generateReport());
    }
}
```

**How it demonstrates:**
- Hides implementation details
- Forces subclasses to implement specific methods
- Provides common functionality in base class
- abstract keyword usage

#### 7. Methods (✅ Multiple Types)

**Types of Methods:**

**a) Instance Methods:**
```java
public String getFullName() {
    return firstName + " " + lastName;
}

public void addPet(Pet pet) {
    pets.add(pet);
}
```

**b) Static Methods:**
```java
public static DatabaseConfig getInstance() {
    return instance;
}

public static boolean validateEmail(String email) {
    return email.contains("@");
}
```

**c) Constructor Methods:**
```java
public PetOwner(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
}
```

**d) Getter/Setter Methods:**
```java
public String getFirstName() { return firstName; }
public void setFirstName(String firstName) { this.firstName = firstName; }
```

**e) Business Logic Methods:**
```java
public boolean validateCredentials() {
    if (username == null || username.trim().isEmpty()) {
        return false;
    }
    if (password == null || password.length() < 6) {
        return false;
    }
    return true;
}
```

#### 8. Exception Handling (✅ Comprehensive)

**Custom Exception Classes:**
1. DatabaseException.java
2. ValidationException.java
3. AuthenticationException.java
4. RecordNotFoundException.java
5. DuplicateRecordException.java

**Try-Catch Implementation:**
```java
public boolean insert(Appointment appointment) throws DatabaseException {
    PreparedStatement stmt = null;
    try {
        beginTransaction();
        stmt = connection.prepareStatement(sql);
        // ... set parameters
        int rows = stmt.executeUpdate();
        commit();
        return rows > 0;
        
    } catch (SQLException e) {
        rollback();
        throw new DatabaseException("Insert failed: " + e.getMessage(), e);
        
    } finally {
        closeStatement(stmt);
    }
}
```

**Exception Hierarchy:**
```
RuntimeException
├── DatabaseException
├── ValidationException
├── AuthenticationException
├── RecordNotFoundException
└── DuplicateRecordException
```

#### 9. Collections (✅ ArrayList Usage)

**Implementation Examples:**

**a) In PetOwner Class:**
```java
private List<Pet> pets = new ArrayList<>();

public void addPet(Pet pet) {
    pets.add(pet);
}

public Pet getPetByName(String name) {
    for (Pet pet : pets) {
        if (pet.getPetName().equalsIgnoreCase(name)) {
            return pet;
        }
    }
    return null;
}
```

**b) In DAO Classes:**
```java
public List<Appointment> findAll() {
    List<Appointment> appointments = new ArrayList<>();
    // ... query database
    while (rs.next()) {
        appointments.add(extractFromResultSet(rs));
    }
    return appointments;
}
```

**c) Search Results:**
```java
public List<String> searchResults(String term) {
    List<String> results = new ArrayList<>();
    // ... perform search
    return results;
}
```

### Class Count Summary

**Total Classes: 45+** ✅ (Requirement: 5+ classes)

- **Model Classes**: 8
- **DAO Classes**: 8
- **Service Classes**: 4
- **UI Classes**: 6
- **Utility Classes**: 4
- **Exception Classes**: 5
- **Report Classes**: 4
- **Configuration Classes**: 2
- **Enum Classes**: 2
- **Main Class**: 1
- **Test Classes**: 2+

---

## 6. 2-Week Sprint Plan

### Team Allocation

**Backend Team (3 developers):**
- **Developer 1**: Authentication, User Management, Core Models
- **Developer 2**: Pet Owner, Pet, Service Management
- **Developer 3**: Appointments, Payments, Reports

**UI/UX Team (2 developers):**
- **Developer 4**: Menu System, Navigation, Validation
- **Developer 5**: UI Design, User Experience, Polish

### Daily Schedule

**Daily Standup: 9:00 AM** (15 minutes)
- What did I complete yesterday?
- What will I work on today?
- Any blockers?

**Integration Sessions:**
- **Day 4 PM**: First integration checkpoint
- **Day 7 PM**: Mid-sprint integration
- **Day 11 AM**: Final integration
- **Day 14 AM**: Final testing

### Week 1: Foundation & Core Development

#### Day 1-2: Setup & Architecture

**Backend Team Tasks:**
- [ ] Set up development environment (JDK, MySQL, IDE)
- [ ] Create project structure and packages
- [ ] Initialize Git repository
- [ ] Create database schema
- [ ] Set up DatabaseConfig (singleton)
- [ ] Implement base model classes (User, enums)
- [ ] Create BaseDAO abstract class

**UI/UX Team Tasks:**
- [ ] Design menu flow diagrams
- [ ] Create UI mockups (console/JOptionPane)
- [ ] Define color scheme and formatting
- [ ] Document UI standards
- [ ] Plan user journey workflows
- [ ] Create UIHelper utility class

**Deliverables:**
- ✅ Database created and tables set up
- ✅ Project structure established
- ✅ Base classes implemented
- ✅ UI design document complete
- ✅ Team coordination established

#### Day 3-4: Core Classes & Database Integration

**Developer 1 Tasks:**
- [ ] Complete User.java (base class)
- [ ] Implement UserDAO.java
- [ ] Create AuthenticationService.java
- [ ] Build login functionality
- [ ] Add password hashing utility
- [ ] Implement session management

**Developer 2 Tasks:**
- [ ] Complete PetOwner.java (extends User)
- [ ] Create Pet.java class
- [ ] Implement PetOwnerDAO.java
- [ ] Create PetDAO.java
- [ ] Add CRUD operations for both
- [ ] Test one-to-many relationship

**Developer 3 Tasks:**
- [ ] Complete Service.java class
- [ ] Complete Employee.java (extends User)
- [ ] Implement ServiceDAO.java
- [ ] Create EmployeeDAO.java
- [ ] Add CRUD operations
- [ ] Test database connections

**Developer 4 Tasks:**
- [ ] Create MenuManager.java
- [ ] Implement LoginUI.java
- [ ] Build main menu structure
- [ ] Add navigation logic
- [ ] Create ConsoleColors.java utility

**Developer 5 Tasks:**
- [ ] Design menu borders and formatting
- [ ] Create loading animations
- [ ] Implement confirmation dialogs
- [ ] Add error message formatting
- [ ] Test UI responsiveness

**Deliverables:**
- ✅ 5+ model classes complete
- ✅ 3+ DAO classes working
- ✅ Login system functional
- ✅ Menu system implemented
- ✅ First integration successful

#### Day 5-7: Appointments & Business Logic

**Developer 1 Tasks:**
- [ ] Create Appointment.java class
- [ ] Implement AppointmentDAO.java
- [ ] Add appointment CRUD operations
- [ ] Implement booking validation logic
- [ ] Add conflict checking
- [ ] Create status management

**Developer 2 Tasks:**
- [ ] Create Payment.java class
- [ ] Implement PaymentDAO.java
- [ ] Link payments to appointments
- [ ] Add payment processing logic
- [ ] Create payment validation

**Developer 3 Tasks:**
- [ ] Implement search functionality
- [ ] Create filter utilities
- [ ] Add sorting methods
- [ ] Build complex queries (JOINs)
- [ ] Start report generation framework

**Developer 4 Tasks:**
- [ ] Create appointment booking UI
- [ ] Implement service selection screen
- [ ] Add date/time picker interface
- [ ] Build appointment list view
- [ ] Add status update interface

**Developer 5 Tasks:**
- [ ] Design confirmation screens
- [ ] Create success/error messages
- [ ] Add progress indicators
- [ ] Implement help screens
- [ ] Polish all UI elements

**Deliverables:**
- ✅ Appointment system complete
- ✅ Payment system integrated
- ✅ Search/filter working
- ✅ All UI screens done
- ✅ Mid-sprint integration successful

### Week 2: Integration, Testing & Documentation

#### Day 8-9: Exception Handling & Validation

**All Backend Developers:**
- [ ] Add try-catch to all DAO methods
- [ ] Implement custom exception classes
- [ ] Add transaction rollback
- [ ] Create error logging
- [ ] Test database error scenarios

**UI/UX Team:**
- [ ] Implement InputValidator.java
- [ ] Add validation to all input fields
- [ ] Create user-friendly error messages
- [ ] Test edge cases
- [ ] Add input sanitization

**All Team:**
- [ ] Conduct integration testing
- [ ] Fix discovered bugs
- [ ] Code review session
- [ ] Performance optimization
- [ ] Refactor duplicate code

**Deliverables:**
- ✅ Exception handling complete
- ✅ Validation implemented everywhere
- ✅ System stable
- ✅ Code cleaned and optimized

#### Day 10-11: Reports & Advanced Features

**Developer 1 Tasks:**
- [ ] Create Report.java (abstract)
- [ ] Implement AppointmentReport.java
- [ ] Create RevenueReport.java
- [ ] Add CustomerReport.java
- [ ] Implement report export

**Developer 2 Tasks:**
- [ ] Add appointment history feature
- [ ] Implement staff assignment logic
- [ ] Create availability checking
- [ ] Add notification system
- [ ] Build dashboard view

**Developer 3 Tasks:**
- [ ] Optimize database queries
- [ ] Add batch operations
- [ ] Implement caching with HashMap
- [ ] Create sorting with Comparator
- [ ] Performance testing

**Developer 4 Tasks:**
- [ ] Design report viewing UI
- [ ] Create statistics display
- [ ] Add dashboard interface
- [ ] Implement help system

**Developer 5 Tasks:**
- [ ] Final UI polish
- [ ] Add visual feedback
- [ ] Create loading screens
- [ ] Test user experience
- [ ] Gather feedback

**Deliverables:**
- ✅ Report generation working
- ✅ Advanced features complete
- ✅ Collections properly used
- ✅ System fully integrated

#### Day 12-13: Documentation & Defense Prep

**All Team Tasks:**

**Documentation:**
- [ ] Write Javadoc for all classes
- [ ] Create user manual
- [ ] Write technical documentation
- [ ] Document ERD and relationships
- [ ] Create UML class diagram
- [ ] Write setup instructions

**Testing:**
- [ ] Create test case document
- [ ] Test all features thoroughly
- [ ] Screenshot every feature
- [ ] Document test results
- [ ] Record demo video

**Presentation:**
- [ ] Create PowerPoint slides
- [ ] Prepare demo script
- [ ] Practice presentation
- [ ] Prepare Q&A answers
- [ ] Create handout materials

**Defense Preparation:**
- [ ] Review OOP concepts
- [ ] Practice explaining code
- [ ] Prepare code walkthrough
- [ ] Review database design
- [ ] Practice as a team

**Deliverables:**
- ✅ Complete documentation package
- ✅ Presentation ready
- ✅ Team prepared for defense
- ✅ All materials finalized

#### Day 14: Final Testing & Submission

**Morning (8:00 AM - 12:00 PM):**
- [ ] Final integration testing
- [ ] Fix any last bugs
- [ ] Verify all requirements met
- [ ] Test on fresh machine
- [ ] Package all files

**Afternoon (1:00 PM - 5:00 PM):**
- [ ] Create submission package
- [ ] Write README.md
- [ ] Verify all documents included
- [ ] Create backup copies
- [ ] Final presentation rehearsal
- [ ] Submit project

**Submission Checklist:**
- [ ] Source code (.java files)
- [ ] Database schema (.sql)
- [ ] User manual (PDF)
- [ ] Technical documentation (PDF)
- [ ] UML diagrams (PNG/PDF)
- [ ] ERD (PNG/PDF)
- [ ] Presentation (PPT)
- [ ] Demo video (optional)
- [ ] README with instructions
- [ ] MySQL connector JAR file

---

## 7. Feature Specifications

### Feature 1: Login Module

**Purpose**: Authenticate users and manage sessions

**Inputs:**
- Username (String, required)
- Password (String, required, min 6 characters)

**Process:**
1. User enters credentials
2. System validates format
3. Check against database
4. Verify password hash
5. Load user role
6. Create session
7. Redirect to role-specific menu

**Outputs:**
- Success: User logged in, session created
- Failure: Error message displayed

**Validation:**
- Username not empty
- Password minimum 6 characters
- User exists in database
- Password matches
- Account is active

**Exception Handling:**
- DatabaseException: Connection failed
- AuthenticationException: Invalid credentials
- ValidationException: Invalid input format

**OOP Concepts:**
- Encapsulation: Password hashing
- Exception Handling: Try-catch blocks
- Object creation: User object from database

### Feature 2: Add Pet Owner Record

**Purpose**: Register new pet owner in system

**Inputs:**
- Username (unique, required)
- Password (min 6 chars)
- Email (valid format, unique)
- Phone (10-11 digits)
- First Name (required)
- Last Name (required)
- Address (optional)
- City (optional)
- Emergency Contact (required)

**Process:**
1. Display input form
2. Validate each field
3. Check for duplicates (username, email)
4. Create User record (parent)
5. Create PetOwner record (child)
6. Hash password
7. Insert into database (transaction)
8. Confirm success

**Outputs:**
- Success message with owner ID
- Error message if validation fails

**Validation Rules:**
- Email must contain @
- Phone 10-11 digits only
- Username minimum 5 characters
- No duplicate username/email
- Required fields not empty

**Database Operations:**
```sql
BEGIN TRANSACTION;
INSERT INTO users (...) VALUES (...);
INSERT INTO pet_owners (user_id, ...) VALUES (LAST_INSERT_ID(), ...);
COMMIT;
```

### Feature 3: Book Appointment

**Purpose**: Create new appointment booking

**Workflow:**
1. Select pet owner (search by name)
2. Select pet from owner's pets
3. Choose service from catalog
4. Pick appointment date
5. Select time slot
6. Assign employee (optional)
7. Calculate price
8. Add notes
9. Confirm booking

**Business Rules:**
- No double booking same employee/time
- Appointment must be future date
- Check employee availability
- Time slots: 9 AM - 5 PM only
- Price = base_price from service
- Status starts as PENDING

**Validation:**
- Pet must belong to owner
- Date not in past
- Time within business hours
- No conflicting appointments
- All required fields present

### Feature 4: Search Appointments

**Search Criteria:**
- By owner name (first or last)
- By pet name
- By appointment date
- By status
- By service type
- By date range

**Implementation:**
```sql
SELECT a.*, po.first_name, po.last_name, p.pet_name, s.service_name
FROM appointments a
JOIN pet_owners po ON a.owner_id = po.owner_id
JOIN pets p ON a.pet_id = p.pet_id
JOIN services s ON a.service_id = s.service_id
WHERE po.first_name LIKE ? OR po.last_name LIKE ?
ORDER BY a.appointment_date DESC;
```

**Display Results:**
- ID | Owner | Pet | Service | Date | Time | Status | Price
- Numbered list for selection
- Option to view details
- Option to update/cancel

### Feature 5: Update Appointment

**Updatable Fields:**
- Appointment date
- Appointment time
- Service (recalculate price)
- Employee assignment
- Status
- Notes

**Process:**
1. Search and select appointment
2. Display current details
3. Show update menu
4. Select field to update
5. Enter new value
6. Validate change
7. Check conflicts (if date/time/employee changed)
8. Update database
9. Update timestamp
10. Confirm success

**Status Transitions:**
```
PENDING → CONFIRMED
CONFIRMED → IN_PROGRESS
IN_PROGRESS → COMPLETED
Any → CANCELLED
```

### Feature 6: Cancel Appointment

**Process:**
1. Search appointment
2. Display details
3. Confirm cancellation
4. Update status to CANCELLED
5. Optionally refund payment
6. Log cancellation
7. Notify owner (console message)

**Validation:**
- Cannot cancel COMPLETED appointments
- Confirmation required
- Update payment status if paid

---

## 8. User Interface Design

### Console Design Standards

**Color Scheme:**
```java
// ConsoleColors.java
public static final String RESET = "\033[0m";
public static final String RED = "\033[0;31m";
public static final String GREEN = "\033[0;32m";
public static final String YELLOW = "\033[0;33m";
public static final String BLUE = "\033[0;34m";
public static final String CYAN = "\033[0;36m";
public static final String CYAN_BOLD = "\033[1;36m";
```

**Usage:**
- **CYAN_BOLD**: Headers, titles
- **GREEN**: Success messages
- **RED**: Error messages
- **YELLOW**: Warnings, prompts
- **BLUE**: Information
- **WHITE**: Regular text

### Menu Structure

**Main Login Screen:**
```
╔════════════════════════════════════════╗
║   🐾 SMART PET BOOKING SYSTEM 🐾     ║
╠════════════════════════════════════════╣
║                                        ║
║   Username: _____________             ║
║   Password: _____________             ║
║                                        ║
║   [1] Login                           ║
║   [2] Register as Pet Owner           ║
║   [0] Exit                            ║
║                                        ║
╚════════════════════════════════════════╝
```

**Admin Main Menu:**
```
╔════════════════════════════════════════╗
║          ADMIN DASHBOARD              ║
╠════════════════════════════════════════╣
║  Welcome, Admin!                      ║
║  Last Login: 2024-06-15 10:30 AM     ║
╠════════════════════════════════════════╣
║                                        ║
║  [1] Manage Appointments              ║
║  [2] Manage Pet Owners                ║
║  [3] Manage Employees                 ║
║  [4] Manage Services                  ║
║  [5] Generate Reports                 ║
║  [6] System Settings                  ║
║  [0] Logout                           ║
║                                        ║
╚════════════════════════════════════════╝
Enter choice:
```

**Pet Owner Main Menu:**
```
╔════════════════════════════════════════╗
║         PET OWNER DASHBOARD           ║
╠════════════════════════════════════════╣
║  Welcome, Maria Santos!               ║
║  Registered Pets: 2                   ║
╠════════════════════════════════════════╣
║                                        ║
║  [1] Book New Appointment             ║
║  [2] View My Appointments             ║
║  [3] Manage My Pets                   ║
║  [4] View Appointment History         ║
║  [5] Update Profile                   ║
║  [0] Logout                           ║
║                                        ║
╚════════════════════════════════════════╝
Enter choice:
```

### Input Patterns

**Text Input:**
```
Enter pet name: _____________
```

**Number Input:**
```
Enter pet age (years): _____
```

**Selection Menu:**
```
Select Pet Species:
[1] Dog
[2] Cat
[3] Bird
[4] Rabbit
[5] Other

Enter choice (1-5): _____
```

**Date Input:**
```
Enter appointment date (YYYY-MM-DD): __________
Example: 2024-06-15
```

**Confirmation:**
```
Are you sure? (Y/N): ____
```

### JOptionPane Dialogs

**Input Dialog:**
```java
String name = JOptionPane.showInputDialog(
    null,
    "Enter pet name:",
    "Add Pet",
    JOptionPane.QUESTION_MESSAGE
);
```

**Confirmation Dialog:**
```java
int choice = JOptionPane.showConfirmDialog(
    null,
    "Are you sure you want to cancel this appointment?",
    "Confirm Cancellation",
    JOptionPane.YES_NO_OPTION,
    JOptionPane.WARNING_MESSAGE
);
```

**Selection Dialog:**
```java
String[] services = {"Grooming", "Pet Sitting", "Walking"};
String selected = (String) JOptionPane.showInputDialog(
    null,
    "Select service:",
    "Book Appointment",
    JOptionPane.QUESTION_MESSAGE,
    null,
    services,
    services[0]
);
```

### Error Message Format

**Console:**
```
[ERROR] Validation Failed
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Field: Email
Issue: Invalid email format
Value entered: 'user@'
Expected format: user@domain.com
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Press Enter to continue...
```

**JOptionPane:**
```java
JOptionPane.showMessageDialog(
    null,
    "Invalid email format!\n\n" +
    "Expected: user@domain.com\n" +
    "Entered: " + email,
    "Validation Error",
    JOptionPane.ERROR_MESSAGE
);
```

### Success Message Format

**Console:**
```
✓ SUCCESS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Appointment booked successfully!

Appointment ID: 12345
Owner: Maria Santos
Pet: Max (Golden Retriever)
Service: Dog Grooming
Date: 2024-06-15
Time: 10:00 AM
Status: CONFIRMED
Total: ₱800.00
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### Loading Animation

**Simple Dots:**
```
Processing...
Processing.
Processing..
Processing...
```

**Spinner:**
```
[|] Loading...
[/] Loading...
[-] Loading...
[\] Loading...
```

---

## 9. Testing Strategy

### Unit Testing Checklist

#### Database Connection Tests
- [ ] Test successful connection
- [ ] Test connection with wrong credentials
- [ ] Test connection timeout
- [ ] Test reconnection logic

#### CRUD Operation Tests

**For Each Entity (User, Pet, Appointment, etc.):**

**CREATE (Insert) Tests:**
- [ ] Insert valid record
- [ ] Insert with missing required field
- [ ] Insert duplicate (unique constraint)
- [ ] Insert with invalid data type
- [ ] Insert with null values

**READ (Select) Tests:**
- [ ] Find by valid ID
- [ ] Find by non-existent ID
- [ ] Find all records
- [ ] Find with no results
- [ ] Search with wildcard

**UPDATE Tests:**
- [ ] Update valid record
- [ ] Update non-existent record
- [ ] Update with invalid data
- [ ] Update unique field to duplicate

**DELETE Tests:**
- [ ] Delete existing record
- [ ] Delete non-existent record
- [ ] Delete with foreign key constraint
- [ ] Soft delete (status change)

#### Validation Tests

**Email Validation:**
- [ ] Valid email: user@domain.com
- [ ] Invalid: missing @
- [ ] Invalid: missing domain
- [ ] Invalid: special characters

**Phone Validation:**
- [ ] Valid: 09171234567 (11 digits)
- [ ] Valid: 02123456789 (10 digits)
- [ ] Invalid: letters included
- [ ] Invalid: too short/long

**Date Validation:**
- [ ] Valid future date
- [ ] Invalid past date
- [ ] Invalid format
- [ ] Leap year handling

**Required Field Validation:**
- [ ] All required fields present
- [ ] Missing required field
- [ ] Empty string in required field
- [ ] Whitespace only

#### Business Logic Tests

**Appointment Booking:**
- [ ] Book with all valid data
- [ ] Book with conflicting time
- [ ] Book in the past
- [ ] Book outside business hours
- [ ] Book for non-existent pet
- [ ] Book without available employee

**Payment Processing:**
- [ ] Process valid payment
- [ ] Process with insufficient amount
- [ ] Process duplicate payment
- [ ] Refund completed payment

**Search Functionality:**
- [ ] Search with valid term
- [ ] Search with no results
- [ ] Search with wildcard
- [ ] Search with special characters

### Integration Testing

**Test Scenarios:**

**Scenario 1: Complete Booking Flow**
1. Login as pet owner
2. Navigate to booking
3. Select pet
4. Choose service
5. Pick date/time
6. Confirm booking
7. Make payment
8. Verify in database
9. Generate receipt

**Scenario 2: Update Flow**
1. Login as employee
2. Search appointment
3. View details
4. Update status
5. Assign employee
6. Save changes
7. Verify in database

**Scenario 3: Cancel Flow**
1. Login as admin
2. Find appointment
3. Cancel with reason
4. Process refund
5. Notify owner
6. Verify status change

### User Acceptance Testing (UAT)

**Test Cases:**

**TC-001: Pet Owner Registration**
- **Given**: New user wants to register
- **When**: User fills registration form
- **Then**: Account created, can login

**TC-002: Book Appointment**
- **Given**: Pet owner logged in with registered pet
- **When**: User books grooming service
- **Then**: Appointment created with PENDING status

**TC-003: Employee Views Schedule**
- **Given**: Employee logged in
- **When**: Employee checks today's appointments
- **Then**: List shows all assigned appointments

**TC-004: Generate Report**
- **Given**: Admin logged in
- **When**: Admin generates monthly revenue report
- **Then**: Report shows all payments for the month

### Test Data

**Sample Pet Owners:**
```
Owner 1: Maria Santos
- Username: maria_santos
- Email: maria@gmail.com
- Phone: 09171234567
- Pet: Max (Dog, Golden Retriever)

Owner 2: Juan Cruz
- Username: juan_cruz
- Email: juan@gmail.com
- Phone: 09181234567
- Pet: Bella (Cat, Persian)
```

**Sample Appointments:**
```
Appointment 1:
- Owner: Maria Santos
- Pet: Max
- Service: Dog Grooming
- Date: 2024-06-15
- Time: 10:00 AM
- Status: CONFIRMED

Appointment 2:
- Owner: Juan Cruz
- Pet: Bella
- Service: Cat Grooming
- Date: 2024-06-16
- Time: 2:00 PM
- Status: PENDING
```

### Bug Tracking Template

| ID | Date | Tester | Description | Severity | Status | Fixed By |
|----|------|--------|-------------|----------|--------|----------|
| B001 | 06/10 | Dev4 | Login crashes on empty password | High | Open | Dev1 |
| B002 | 06/11 | Dev5 | Date picker shows past dates | Medium | Fixed | Dev4 |
| B003 | 06/12 | Dev1 | Duplicate username not caught | High | Fixed | Dev1 |

**Severity Levels:**
- **Critical**: System crash, data loss
- **High**: Feature doesn't work
- **Medium**: Feature works but with issues
- **Low**: Cosmetic, typos

---

## 10. Report Generation

### Report Types

#### 1. Appointment Report

**Purpose**: View all appointments within date range

**Parameters:**
- Start Date
- End Date
- Status filter (optional)
- Employee filter (optional)

**Output Format:**
```
╔══════════════════════════════════════════════════════════╗
║            APPOINTMENT REPORT                            ║
║          Date Range: 2024-06-01 to 2024-06-30           ║
╠══════════════════════════════════════════════════════════╣
║  Total Appointments: 45                                  ║
║  Completed: 30                                           ║
║  Pending: 10                                             ║
║  Cancelled: 5                                            ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  ID    Date       Pet      Service       Status  Price  ║
║  ───────────────────────────────────────────────────────║
║  101   06-05   Max      Grooming      Completed  ₱800  ║
║  102   06-06   Bella    Sitting       Completed  ₱1500 ║
║  103   06-07   Rocky    Walking       Pending    ₱300  ║
║  ...                                                     ║
║                                                          ║
║  TOTAL REVENUE: ₱34,500.00                              ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

**SQL Query:**
```sql
SELECT 
    a.appointment_id,
    a.appointment_date,
    p.pet_name,
    s.service_name,
    a.status,
    a.total_price
FROM appointments a
JOIN pets p ON a.pet_id = p.pet_id
JOIN services s ON a.service_id = s.service_id
WHERE a.appointment_date BETWEEN ? AND ?
ORDER BY a.appointment_date DESC;
```

#### 2. Revenue Report

**Purpose**: Financial summary for accounting

**Parameters:**
- Month/Year
- Payment method filter (optional)

**Calculations:**
- Total Revenue = SUM(payments.amount)
- Average Per Appointment = Total / Count
- Revenue by Service Type
- Revenue by Payment Method

**Output:**
```
╔══════════════════════════════════════════════════════════╗
║               MONTHLY REVENUE REPORT                      ║
║                  June 2024                                ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  SUMMARY                                                 ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  Total Appointments: 45                                  ║
║  Completed Appointments: 40                              ║
║  Total Revenue: ₱54,300.00                              ║
║  Average per Appointment: ₱1,357.50                      ║
║                                                          ║
║  REVENUE BY SERVICE                                      ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  Dog Grooming        20 appts    ₱16,000.00            ║
║  Cat Grooming        10 appts    ₱6,000.00             ║
║  Pet Sitting         8 appts     ₱12,000.00            ║
║  Dog Walking         12 appts    ₱3,600.00             ║
║                                                          ║
║  PAYMENT METHODS                                         ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  Cash                           ₱32,580.00  (60%)       ║
║  GCash                          ₱16,290.00  (30%)       ║
║  Credit Card                    ₱5,430.00   (10%)       ║
║                                                          ║
║  DAILY BREAKDOWN                                         ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  Week 1 (Jun 1-7)    8 appts     ₱10,800.00            ║
║  Week 2 (Jun 8-14)   10 appts    ₱13,500.00            ║
║  Week 3 (Jun 15-21)  12 appts    ₱16,200.00            ║
║  Week 4 (Jun 22-30)  10 appts    ₱13,800.00            ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝

Report Generated: 2024-06-30 09:15 AM
Generated By: Admin User
```

#### 3. Customer Activity Report

**Purpose**: Track customer engagement

**Metrics:**
- Total customers registered
- Active customers (booked in period)
- New customers
- Repeat customers
- Average appointments per customer
- Top 10 customers by bookings

**Output:**
```
╔══════════════════════════════════════════════════════════╗
║          CUSTOMER ACTIVITY REPORT                         ║
║               June 2024                                   ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  CUSTOMER METRICS                                        ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  Total Registered: 87                                    ║
║  Active This Month: 35                                   ║
║  New Registrations: 12                                   ║
║  Retention Rate: 85%                                     ║
║  Average Bookings per Customer: 1.5                      ║
║                                                          ║
║  TOP 10 CUSTOMERS                                        ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  #   Name              Pets  Bookings  Total Spent      ║
║  ─────────────────────────────────────────────────────  ║
║  1.  Maria Santos      2     8         ₱6,400.00       ║
║  2.  Juan Cruz         1     5         ₱3,750.00       ║
║  3.  Ana Reyes         3     4         ₱5,200.00       ║
║  4.  Pedro Gomez       1     4         ₱3,200.00       ║
║  5.  Sofia Lim         2     3         ₱4,500.00       ║
║  ...                                                     ║
║                                                          ║
║  NEW CUSTOMERS THIS MONTH                                ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  Name              Registration Date    First Booking   ║
║  ──────────────────────────────────────────────────────║
║  Carlos Tan        2024-06-03          2024-06-05      ║
║  Lisa Wong         2024-06-07          2024-06-09      ║
║  ...                                                     ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

#### 4. Employee Performance Report

**Purpose**: Track employee productivity

**Metrics:**
- Appointments handled
- Revenue generated
- Average rating (if implemented)
- Punctuality
- Customer satisfaction

**Output:**
```
╔══════════════════════════════════════════════════════════╗
║        EMPLOYEE PERFORMANCE REPORT                        ║
║               June 2024                                   ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  Employee: John Doe                                      ║
║  Position: Groomer                                       ║
║  Specialization: Dog Grooming                            ║
║                                                          ║
║  PERFORMANCE METRICS                                     ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  Appointments Completed: 28                              ║
║  No-Shows: 2                                             ║
║  Cancellations: 1                                        ║
║  Revenue Generated: ₱22,400.00                          ║
║  Average per Appointment: ₱800.00                        ║
║                                                          ║
║  APPOINTMENT BREAKDOWN                                   ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  Dog Grooming: 20 appointments                           ║
║  Cat Grooming: 8 appointments                            ║
║                                                          ║
║  WEEKLY DISTRIBUTION                                     ║
║  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  ║
║  Week 1:  6 appointments                                 ║
║  Week 2:  8 appointments                                 ║
║  Week 3:  7 appointments                                 ║
║  Week 4:  7 appointments                                 ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

### Export Options

**1. Console Display** (default)
- Print to screen
- User can view immediately

**2. Text File Export**
```java
public void exportToFile(String report, String filename) {
    try (BufferedWriter writer = new BufferedWriter(
            new FileWriter("reports/" + filename + ".txt"))) {
        writer.write(report);
        System.out.println("Report saved to: reports/" + filename + ".txt");
    } catch (IOException e) {
        throw new DatabaseException("Export failed: " + e.getMessage(), e);
    }
}
```

**3. CSV Export** (bonus feature)
```java
public void exportToCSV(List<Appointment> appointments, String filename) {
    try (PrintWriter writer = new PrintWriter("reports/" + filename + ".csv")) {
        writer.println("ID,Date,Pet,Service,Status,Price");
        for (Appointment apt : appointments) {
            writer.printf("%d,%s,%s,%s,%s,%.2f%n",
                apt.getAppointmentId(),
                apt.getAppointmentDate(),
                apt.getPetName(),
                apt.getServiceName(),
                apt.getStatus(),
                apt.getTotalPrice()
            );
        }
    }
}
```

---

## 11. Defense Preparation

### Common Questions & Answers

#### General Project Questions

**Q1: What is your system about?**

**A:** Our Smart Pet Booking System is a centralized appointment management system designed for pet care businesses in the Philippines. It replaces manual booking methods like handwritten logs and Facebook Messenger reservations with an organized, database-driven solution. The system allows pet owners to register their pets and book appointments, while employees and administrators can manage schedules, track service history, and generate reports. It solves problems like scheduling conflicts, lost records, and duplicate client information.

**Q2: Why did you choose this topic?**

**A:** We chose this topic because:
1. Many small pet care businesses in the Philippines still use manual systems
2. We saw a real need for affordable, accessible booking solutions
3. It demonstrates all required OOP concepts naturally
4. It's practical and can be used in real businesses
5. It combines our interests in software development and helping local businesses

**Q3: Who are the target users?**

**A:** Our system has three types of users:
1. **Pet Owners**: Register pets, book appointments, view history
2. **Employees**: View schedules, update appointment status, access client information
3. **Administrators**: Complete system access, manage all records, generate reports

#### Technical Questions

**Q4: Explain your database design.**

**A:** We have 7 tables following 3NF (Third Normal Form):
1. **users** - Base table for authentication (stores username, password, role)
2. **pet_owners** - Extends users, stores owner details (one-to-one with users)
3. **employees** - Extends users, stores employee details (one-to-one with users)
4. **pets** - Pet profiles (one-to-many with pet_owners)
5. **services** - Service catalog (grooming, sitting, walking)
6. **appointments** - Booking records (links owners, pets, services, employees)
7. **payments** - Payment transactions (one-to-one with appointments)

The relationships are:
- One owner can have many pets
- One pet can have many appointments
- One service can be booked many times
- One appointment has one payment

**Q5: How did you implement inheritance?**

**A:** We implemented inheritance in three places:

1. **User Hierarchy** (most important):
```
User (base class with username, password, email)
  ├─ PetOwner extends User (adds firstName, lastName, address)
  └─ Employee extends User (adds position, specialization, hireDate)
```

2. **DAO Hierarchy**:
```
BaseDAO<T> (abstract class with common database methods)
  ├─ UserDAO extends BaseDAO<User>
  ├─ AppointmentDAO extends BaseDAO<Appointment>
  └─ (all other DAOs)
```

3. **Report Hierarchy**:
```
Report (abstract class with generateReport() method)
  ├─ AppointmentReport extends Report
  ├─ RevenueReport extends Report
  └─ CustomerReport extends Report
```

**Q6: How did you implement polymorphism?**

**A:** We use polymorphism in several ways:

1. **Method Overriding**:
```java
// In User class
public String getDisplayName() {
    return username;
}

// In PetOwner class - OVERRIDES parent
@Override
public String getDisplayName() {
    return firstName + " " + lastName;
}
```

2. **Abstract Method Implementation**:
```java
// BaseDAO declares abstract method
public abstract boolean insert(T entity);

// Each DAO implements it differently
// AppointmentDAO.insert() - handles appointments
// PetDAO.insert() - handles pets
```

3. **Polymorphic References**:
```java
User user = new PetOwner(...);  // Parent reference, child object
System.out.println(user.getDisplayName());  // Calls PetOwner version
```

**Q7: Where is abstraction demonstrated?**

**A:** We use abstraction through abstract classes:

1. **BaseDAO<T>** - Abstract base for all DAOs
   - Declares abstract methods: insert(), update(), delete(), findById()
   - Provides concrete helper methods: executeUpdate(), executeQuery()
   - Forces all DAOs to implement CRUD operations
   - Hides database complexity from UI layer

2. **Report** - Abstract base for reports
   - Declares abstract method: generateReport()
   - Each report type implements it differently
   - Common printing logic in base class

**Q8: How did you handle exceptions?**

**A:** We created 5 custom exception classes:

1. **DatabaseException** - Database operation failures
2. **ValidationException** - Input validation failures
3. **AuthenticationException** - Login failures
4. **RecordNotFoundException** - Record not found
5. **DuplicateRecordException** - Unique constraint violations

Example usage:
```java
try {
    appointmentDAO.insert(appointment);
} catch (ValidationException e) {
    System.err.println("Invalid data: " + e.getMessage());
} catch (DatabaseException e) {
    System.err.println("Database error: " + e.getMessage());
    rollback();
} finally {
    closeConnection();
}
```

Every DAO method has try-catch blocks and throws appropriate exceptions.

**Q9: Where did you use Collections?**

**A:** We use ArrayList extensively:

1. **In Models**:
```java
public class PetOwner {
    private List<Pet> pets = new ArrayList<>();  // Owner's pets
}
```

2. **In DAOs** (search results):
```java
public List<Appointment> findAll() {
    List<Appointment> appointments = new ArrayList<>();
    // ... query database and add to list
    return appointments;
}
```

3. **In Services** (filtering):
```java
List<Appointment> filtered = new ArrayList<>();
for (Appointment apt : allAppointments) {
    if (apt.getStatus() == AppointmentStatus.PENDING) {
        filtered.add(apt);
    }
}
```

**Q10: Explain encapsulation in your project.**

**A:** All our model classes use encapsulation:

```java
public class Pet {
    // Private fields - data hiding
    private int petId;
    private String petName;
    private int ownerId;
    
    // Public getters - controlled read access
    public int getPetId() {
        return petId;
    }
    
    // Public setters - controlled write access with validation
    public void setPetName(String petName) {
        if (petName == null || petName.trim().isEmpty()) {
            throw new ValidationException("Pet name cannot be empty");
        }
        this.petName = petName.trim();
    }
}
```

Benefits:
- Fields cannot be directly accessed/modified
- Validation in setters ensures data integrity
- Internal implementation can change without affecting other code

#### OOP Concept Questions

**Q11: What's the difference between inheritance and polymorphism?**

**A:** 
- **Inheritance** = Relationship between classes (IS-A)
  - Example: PetOwner IS-A User
  - Code reuse - child gets parent's properties/methods
  
- **Polymorphism** = Different behavior with same interface
  - Example: `user.getDisplayName()` behaves differently depending on type
  - Runtime decision - which method to call decided at runtime

Both work together - we need inheritance to enable polymorphism.

**Q12: Why use abstract classes instead of regular classes?**

**A:** Abstract classes provide:
1. **Template/Blueprint** - Define structure without implementation
2. **Force Implementation** - Subclasses MUST implement abstract methods
3. **Share Common Code** - Concrete methods shared by all children
4. **Prevent Direct Instantiation** - Can't create BaseDAO object directly

Example: BaseDAO provides executeUpdate() for all DAOs, but each DAO must implement insert() differently.

**Q13: What's the difference between private, protected, and public?**

**A:**
- **private**: Only within same class
  - Example: `private int userId` in User
  
- **protected**: Within same class + subclasses
  - Example: `protected Connection connection` in BaseDAO
  - Child DAOs can access it
  
- **public**: Accessible everywhere
  - Example: `public String getUsername()` can be called from UI

**Q14: Why use interfaces vs abstract classes?**

**A:** We didn't use interfaces in this project, but the difference is:
- **Abstract Class**: Can have both abstract AND concrete methods, can have fields
- **Interface**: Only abstract methods (Java 8+ allows default methods)
- **When to use**: 
  - Abstract class when sharing code (like BaseDAO)
  - Interface when defining contract only

#### Implementation Questions

**Q15: How does your login system work?**

**A:** Login flow:
1. User enters username and password
2. System validates format (username not empty, password min 6 chars)
3. Query database: `SELECT * FROM users WHERE username = ?`
4. Compare entered password with stored password (hashed)
5. If match, load user role (ADMIN/EMPLOYEE/PET_OWNER)
6. Create session object
7. Redirect to role-specific menu
8. Update last_login timestamp

Security:
- Passwords are hashed (not stored plain text)
- SQL injection prevented using PreparedStatement
- Account lockout after 5 failed attempts

**Q16: How do you prevent double booking?**

**A:** Before inserting appointment, we check:
```java
private boolean hasConflict(Appointment appointment) {
    String sql = "SELECT COUNT(*) FROM appointments " +
                 "WHERE appointment_date = ? " +
                 "AND appointment_time = ? " +
                 "AND employee_id = ? " +
                 "AND status NOT IN ('CANCELLED', 'COMPLETED')";
    // If count > 0, conflict exists
}
```

If conflict found, throw ValidationException with message: "Time slot already booked"

**Q17: How do you handle database errors?**

**A:** Three-layer error handling:

1. **DAO Layer** - Catches SQLException, throws custom exception:
```java
try {
    stmt.executeUpdate();
} catch (SQLException e) {
    throw new DatabaseException("Insert failed: " + e.getMessage(), e);
}
```

2. **Service Layer** - Catches DAO exceptions, adds context:
```java
try {
    appointmentDAO.insert(apt);
} catch (DatabaseException e) {
    throw new DatabaseException("Booking failed: " + e.getMessage(), e);
}
```

3. **UI Layer** - Catches all exceptions, shows user-friendly message:
```java
try {
    service.bookAppointment(...);
    showSuccess("Appointment booked!");
} catch (Exception e) {
    showError("Booking failed: " + e.getMessage());
}
```

**Q18: What's your database connection strategy?**

**A:** We use Singleton Pattern in DatabaseConfig:
```java
public class DatabaseConfig {
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Read from config.properties
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }
}
```

Benefits:
- Single shared connection (not opening/closing repeatedly)
- Efficient resource usage
- Centralized configuration

**Q19: How do you validate user input?**

**A:** We have InputValidator utility class:
```java
public class InputValidator {
    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10,11}");
    }
    
    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
```

Used before every database operation.

**Q20: Can you trace the code flow for booking an appointment?**

**A:** Complete flow:

1. **UI Layer** (PetOwnerUI.java):
   ```
   User clicks "Book Appointment"
   → Display service selection menu
   → Collect date, time, pet selection
   → Call appointmentService.bookAppointment(...)
   ```

2. **Service Layer** (AppointmentService.java):
   ```
   → Validate all inputs
   → Check for conflicts
   → Calculate total price
   → Call appointmentDAO.insert(appointment)
   ```

3. **DAO Layer** (AppointmentDAO.java):
   ```
   → Begin transaction
   → Execute INSERT SQL
   → Get generated appointment_id
   → Commit transaction
   → Return success
   ```

4. **Back to UI**:
   ```
   → Display success message
   → Show appointment details
   → Return to main menu
   ```

#### Challenges & Solutions

**Q21: What was the biggest challenge?**

**A:** Database relationships and foreign key constraints. Initially, we couldn't delete a pet owner because they had appointments. 

**Solution:** We used:
- `ON DELETE CASCADE` for pets (delete owner → delete their pets)
- `ON DELETE SET NULL` for employee_id in appointments (delete employee → appointments remain but unassigned)
- Soft deletes for important records (set is_active = false instead of DELETE)

**Q22: How did you divide the work?**

**A:** We divided by layers and features:
- **Dev 1**: Authentication, User management, Base classes
- **Dev 2**: Pet Owner, Pet, Service modules
- **Dev 3**: Appointments, Payments, Reports
- **Dev 4**: Menu system, Navigation, UI flow
- **Dev 5**: UI design, Validation, Polish

We integrated daily to catch conflicts early.

**Q23: What would you improve if you had more time?**

**A:**
1. **Email notifications** - Send confirmation emails
2. **SMS reminders** - Text reminders day before appointment
3. **Online payment** - Integrate payment gateway
4. **Calendar view** - Visual calendar instead of text list
5. **Photo upload** - Upload pet photos
6. **Rating system** - Customer can rate employees
7. **Mobile app** - Android/iOS version
8. **Dashboard charts** - Visual graphs for reports

#### Demonstration Questions

**Q24: Can you show us the login process?**

**A:** [Live demonstration]
1. Run Main.java
2. Enter username: "maria_santos"
3. Enter password: "password123"
4. Show successful login
5. Display pet owner dashboard
6. Show menu options

**Q25: Can you demonstrate error handling?**

**A:** [Live demonstration]
1. Try to book appointment with past date
   → Show error: "Appointment date cannot be in the past"
2. Try to register with existing username
   → Show error: "Username already exists"
3. Try to login with wrong password
   → Show error: "Invalid credentials"

**Q26: Can you show a database query?**

**A:** [Open MySQL Workbench]
```sql
SELECT 
    a.appointment_id,
    CONCAT(po.first_name, ' ', po.last_name) AS owner_name,
    p.pet_name,
    s.service_name,
    a.appointment_date,
    a.status
FROM appointments a
JOIN pet_owners po ON a.owner_id = po.owner_id
JOIN pets p ON a.pet_id = p.pet_id
JOIN services s ON a.service_id = s.service_id
WHERE a.status = 'PENDING'
ORDER BY a.appointment_date;
```

#### Project Requirements Checklist

**Q27: How do you meet all the requirements?**

**A:**
✅ **Language**: Java (JDK 11)
✅ **Interface**: Scanner (console) + JOptionPane (GUI dialogs)
✅ **Database**: MySQL 8.0
✅ **OOP Principles**: All demonstrated (inheritance, polymorphism, encapsulation, abstraction)
✅ **Menu-driven**: MenuManager with role-based menus
✅ **CRUD**: All entities have Create, Read, Update, Delete
✅ **Exception Handling**: 5 custom exception classes + try-catch everywhere
✅ **Input Validation**: InputValidator utility + validation in setters
✅ **5+ Classes**: 45+ classes total
✅ **3+ Tables**: 7 database tables
✅ **10 Features**: All required features implemented
✅ **Collections**: ArrayList used extensively
✅ **Constructors**: Default + parameterized + full constructors

---

## 12. Code Examples & Best Practices

### Coding Standards

#### Naming Conventions

**Classes**: PascalCase
```java
public class PetOwner { }
public class AppointmentService { }
```

**Methods**: camelCase
```java
public void bookAppointment() { }
public String getDisplayName() { }
```

**Variables**: camelCase
```java
private int appointmentId;
private String firstName;
```

**Constants**: UPPER_SNAKE_CASE
```java
public static final int MAX_APPOINTMENTS = 100;
public static final String DATE_FORMAT = "yyyy-MM-DD";
```

**Packages**: lowercase
```java
package dao;
package utils;
```

#### Code Documentation

**Class Javadoc:**
```java
/**
 * PetOwner class - Represents a pet owner in the system
 * 
 * This class extends the User base class and adds pet owner specific
 * information such as personal details and pet collection management.
 * 
 * Demonstrates: Inheritance, Collections, Polymorphism
 * 
 * @author Smart Pet Booking Team
 * @version 1.0
 * @since 2024-06-01
 */
public class PetOwner extends User {
    // ...
}
```

**Method Javadoc:**
```java
/**
 * Book a new appointment for a pet
 * 
 * This method validates the booking details, checks for conflicts,
 * and creates a new appointment record in the database.
 * 
 * @param petId The ID of the pet for the appointment
 * @param serviceId The ID of the requested service
 * @param date The appointment date (must be future date)
 * @param time The appointment time (business hours only)
 * @return The created Appointment object with generated ID
 * @throws ValidationException if input data is invalid
 * @throws DatabaseException if database operation fails
 */
public Appointment bookAppointment(int petId, int serviceId, 
                                   LocalDate date, LocalTime time) 
        throws ValidationException, DatabaseException {
    // ...
}
```

#### Code Structure Best Practices

**1. Single Responsibility**
```java
// GOOD - Each class has one purpose
public class Pet { }  // Represents pet data
public class PetDAO { }  // Handles pet database operations
public class PetService { }  // Handles pet business logic

// BAD - Class doing too much
public class Pet {
    // Data fields
    // Database operations
    // UI methods
    // Validation
    // Everything mixed together
}
```

**2. DRY (Don't Repeat Yourself)**
```java
// GOOD - Reusable method
public void displayAppointment(Appointment apt) {
    System.out.printf("ID: %d | Date: %s | Status: %s%n",
        apt.getId(), apt.getDate(), apt.getStatus());
}

// Use it everywhere
displayAppointment(appointment1);
displayAppointment(appointment2);

// BAD - Duplicate code
System.out.printf("ID: %d | Date: %s | Status: %s%n", ...);
System.out.printf("ID: %d | Date: %s | Status: %s%n", ...);
```

**3. Proper Error Messages**
```java
// GOOD - Helpful error message
if (petName == null || petName.trim().isEmpty()) {
    throw new ValidationException(
        "Pet name is required. Please enter a valid name.");
}

// BAD - Vague error
if (petName == null) {
    throw new Exception("Error");
}
```

**4. Resource Cleanup**
```java
// GOOD - Always close resources
public List<Pet> findAll() {
    ResultSet rs = null;
    PreparedStatement stmt = null;
    try {
        stmt = connection.prepareStatement(sql);
        rs = stmt.executeQuery();
        // ... process results
    } catch (SQLException e) {
        throw new DatabaseException("Query failed", e);
    } finally {
        closeResultSet(rs);
        closeStatement(stmt);
    }
}
```

### Design Pattern Examples

#### 1. Singleton Pattern (DatabaseConfig)
```java
public class DatabaseConfig {
    private static DatabaseConfig instance = null;
    private Connection connection;
    
    // Private constructor
    private DatabaseConfig() throws SQLException {
        // Load config from properties file
        Properties props = new Properties();
        props.load(new FileInputStream("config.properties"));
        
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        
        connection = DriverManager.getConnection(url, user, password);
    }
    
    // Public accessor
    public static DatabaseConfig getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
}
```

#### 2. Factory Pattern (Report Generation)
```java
public class ReportFactory {
    public static Report createReport(ReportType type) {
        switch (type) {
            case APPOINTMENT:
                return new AppointmentReport();
            case REVENUE:
                return new RevenueReport();
            case CUSTOMER:
                return new CustomerReport();
            case EMPLOYEE:
                return new EmployeeReport();
            default:
                throw new IllegalArgumentException("Unknown report type");
        }
    }
}

// Usage
Report report = ReportFactory.createReport(ReportType.REVENUE);
String output = report.generateReport();
```

#### 3. Template Method Pattern (BaseDAO)
```java
public abstract class BaseDAO<T> {
    // Template method - defines algorithm skeleton
    public boolean save(T entity) {
        try {
            beginTransaction();
            boolean result = performSave(entity);
            commit();
            return result;
        } catch (Exception e) {
            rollback();
            throw e;
        }
    }
    
    // Abstract step - subclasses implement
    protected abstract boolean performSave(T entity);
}
```

### Common Pitfalls to Avoid

**1. SQL Injection**
```java
// BAD - Vulnerable to SQL injection
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(sql);

// GOOD - Use PreparedStatement
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, username);
ResultSet rs = stmt.executeQuery();
```

**2. Not Closing Resources**
```java
// BAD - Resource leak
ResultSet rs = stmt.executeQuery(sql);
// ... use rs
// Never closed

// GOOD - Always close in finally
ResultSet rs = null;
try {
    rs = stmt.executeQuery(sql);
    // ... use rs
} finally {
    if (rs != null) rs.close();
}
```

**3. Catching Generic Exception**
```java
// BAD - Hiding specific errors
try {
    // ...
} catch (Exception e) {
    // What kind of error?
}

// GOOD - Catch specific exceptions
try {
    // ...
} catch (SQLException e) {
    // Database error
} catch (ValidationException e) {
    // Validation error
}
```

**4. No Input Validation**
```java
// BAD - Trusting user input
public void setPetAge(int age) {
    this.age = age;  // What if age is -5 or 1000?
}

// GOOD - Validate first
public void setPetAge(int age) {
    if (age < 0 || age > 30) {
        throw new ValidationException("Invalid age: " + age);
    }
    this.age = age;
}
```

### Testing Code Snippets

#### Unit Test Example
```java
public class PetOwnerTest {
    @Test
    public void testGetFullName() {
        PetOwner owner = new PetOwner();
        owner.setFirstName("Juan");
        owner.setLastName("Cruz");
        
        String fullName = owner.getFullName();
        
        assertEquals("Juan Cruz", fullName);
    }
    
    @Test(expected = ValidationException.class)
    public void testInvalidEmail() {
        PetOwner owner = new PetOwner();
        owner.setEmail("invalid-email");  // Should throw
    }
}
```

#### Integration Test Example
```java
public void testCompleteBookingFlow() {
    // 1. Login
    User user = authService.login("maria_santos", "password123");
    assertNotNull(user);
    
    // 2. Get owner
    PetOwner owner = petOwnerDAO.findByUserId(user.getUserId());
    assertNotNull(owner);
    
    // 3. Get pet
    List<Pet> pets = petDAO.findByOwnerId(owner.getOwnerId());
    assertTrue(pets.size() > 0);
    
    // 4. Create appointment
    Appointment apt = new Appointment();
    apt.setPetId(pets.get(0).getPetId());
    apt.setOwnerId(owner.getOwnerId());
    apt.setServiceId(1);  // Grooming
    apt.setAppointmentDate(LocalDate.now().plusDays(1));
    apt.setAppointmentTime(LocalTime.of(10, 0));
    
    // 5. Book
    boolean success = appointmentDAO.insert(apt);
    assertTrue(success);
    assertTrue(apt.getAppointmentId() > 0);
}
```

---

## Appendix A: SQL Quick Reference

### Basic Queries

**Select All:**
```sql
SELECT * FROM pets;
```

**Select Specific Columns:**
```sql
SELECT pet_name, species, age FROM pets;
```

**With WHERE Clause:**
```sql
SELECT * FROM appointments WHERE status = 'PENDING';
```

**With JOIN:**
```sql
SELECT p.pet_name, po.first_name, po.last_name
FROM pets p
JOIN pet_owners po ON p.owner_id = po.owner_id;
```

**Count:**
```sql
SELECT COUNT(*) AS total FROM appointments;
```

**Sum:**
```sql
SELECT SUM(total_price) AS revenue FROM appointments
WHERE status = 'COMPLETED';
```

**Group By:**
```sql
SELECT status, COUNT(*) AS count
FROM appointments
GROUP BY status;
```

### Useful Queries for Testing

**View all appointments with owner and pet names:**
```sql
SELECT 
    a.appointment_id,
    CONCAT(po.first_name, ' ', po.last_name) AS owner,
    p.pet_name AS pet,
    s.service_name AS service,
    a.appointment_date,
    a.appointment_time,
    a.status,
    a.total_price
FROM appointments a
JOIN pet_owners po ON a.owner_id = po.owner_id
JOIN pets p ON a.pet_id = p.pet_id
JOIN services s ON a.service_id = s.service_id
ORDER BY a.appointment_date DESC;
```

**Find available time slots:**
```sql
SELECT appointment_time, COUNT(*) as bookings
FROM appointments
WHERE appointment_date = '2024-06-15'
AND status != 'CANCELLED'
GROUP BY appointment_time
HAVING bookings < 3;  -- Assuming 3 employees available
```

**Monthly revenue:**
```sql
SELECT 
    MONTH(appointment_date) AS month,
    COUNT(*) AS appointments,
    SUM(total_price) AS revenue
FROM appointments
WHERE YEAR(appointment_date) = 2024
AND status = 'COMPLETED'
GROUP BY MONTH(appointment_date)
ORDER BY month;
```

---

## Appendix B: Keyboard Shortcuts

### IDE Shortcuts (IntelliJ IDEA)

- **Ctrl + Space**: Code completion
- **Ctrl + /**: Comment/uncomment line
- **Ctrl + D**: Duplicate line
- **Ctrl + Y**: Delete line
- **Ctrl + Alt + L**: Format code
- **Shift + F10**: Run program
- **Shift + F9**: Debug program

### MySQL Workbench

- **Ctrl + Enter**: Execute current statement
- **Ctrl + Shift + Enter**: Execute all statements
- **F5**: Refresh
- **Ctrl + T**: New query tab

---

## Appendix C: Submission Checklist

### Files to Submit

#### Source Code
- [ ] All .java files in correct package structure
- [ ] config.properties file (with placeholder values)
- [ ] README.md with setup instructions

#### Database
- [ ] schema.sql (CREATE TABLE statements)
- [ ] sample_data.sql (INSERT statements for testing)

#### Documentation
- [ ] User Manual (PDF) - How to use the system
- [ ] Technical Documentation (PDF) - System design and code explanation
- [ ] Project Report (PDF) - Overview, methodology, conclusion

#### Diagrams
- [ ] ERD (Entity Relationship Diagram) - PNG or PDF
- [ ] UML Class Diagram - PNG or PDF
- [ ] Flowchart - PNG or PDF
- [ ] System Architecture Diagram

#### Presentation
- [ ] PowerPoint slides (15-20 slides)
- [ ] Demo script
- [ ] Handout materials for panel

#### Optional
- [ ] Demo video (5-10 minutes)
- [ ] Test cases document
- [ ] Bug tracking report
- [ ] Meeting minutes

### Packaging Instructions

1. Create folder: `SmartPetBookingSystem_GroupName`
2. Organize subfolders:
   ```
   SmartPetBookingSystem_GroupName/
   ├── src/ (all Java files)
   ├── lib/ (MySQL connector JAR)
   ├── database/ (SQL files)
   ├── docs/ (all documentation PDFs)
   ├── diagrams/ (ERD, UML, etc.)
   ├── presentation/ (PPT file)
   └── README.md
   ```
3. Compress to ZIP file
4. Name: `SmartPetBooking_GroupName_Section.zip`
5. Upload to submission platform

---

## Conclusion

This comprehensive documentation provides everything needed to successfully complete the Smart Pet Booking System project. Remember:

1. **Follow the 2-week sprint plan** closely
2. **Communicate daily** with your team
3. **Test frequently** - don't wait until the end
4. **Document as you code** - write Javadoc comments
5. **Practice your presentation** - be confident in defense
6. **Ask for help** when stuck - don't waste time
7. **Have fun!** This is a learning experience

### Success Criteria
- ✅ All 10 required features working
- ✅ All OOP concepts demonstrated
- ✅ Clean, well-documented code
- ✅ Professional presentation
- ✅ Team members can explain any part

### Target Grade: 95%+

Good luck with your project! 🐾

---

**Document Version**: 1.0  
**Last Updated**: June 2024  
**Created by**: Smart Pet Booking System Development Team
**For**: Object-Oriented Programming with Database Integration - Final Project
