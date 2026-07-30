# Library Management System (Java Swing GUI)

A lightweight desktop-based Library Management System built using Java Swing and SQLite database. This application provides a clean graphical user interface to manage books, users, library transactions, audit logs, and PDF reporting.

---

## Key Features

- **Book Management (CRUD):** Add, edit, delete, and view books in real-time.
- **Issue and Return System:** Issue books to registered users and track returned books.
- **User Management:** View all users along with their total borrowed book count and titles.
- **Global Search and Filter:** Search books by ID, Title, or Author, and filter by status (All Books, Available, Issued).
- **Audit Activity Logs:** View complete history of all library operations (Add, Edit, Issue, Return, Delete) with timestamps.
- **PDF Export and Print Support:** Print or save the Books Inventory and Audit Logs directly as PDF files.
- **Database Storage:** Uses SQLite (library.db) for local persistent data storage.

---

## Tech Stack and Requirements

- **Programming Language:** Java (JDK 8 or higher)
- **GUI Framework:** Java Swing (javax.swing)
- **Database:** SQLite
- **Database Driver:** JDBC SQLite (sqlite-jdbc)

---

## Project Structure

```text
LibraryManagementSystem/
├── src/
│   └── HARSH/
│       └── Librarymanagementsystem.java        # Main Source Code
├── library.db                                  # SQLite Database File
└── README.md                                   # Documentation
|--Library management system images & pdf       # Outputs images and pdf                         
How to Run the Project
Option 1: Using Terminal / Command Line
Clone the repository:

Bash
git clone [https://github.com/your-username/Library-Management-System.git](https://github.com/your-username/Library-Management-System.git)
cd Library-Management-System
Compile the Java file with SQLite JDBC driver:

Bash
javac -cp ".;sqlite-jdbc.jar" src/HARSH/Librarymanagementsystem.java -d bin
Run the Application:

Bash
java -cp "bin;sqlite-jdbc.jar" HARSH.Librarymanagementsystem
(Note: Use : instead of ; on Linux/Mac).

Option 2: Using IDE (IntelliJ IDEA / Eclipse / VS Code)
Open the project folder in your IDE.

Add sqlite-jdbc.jar to your project build path / dependencies.

Open Librarymanagementsystem.java located in src/HARSH/.

Run the file as a Java Application.

Database Schema
The application automatically creates and manages three tables in library.db:

books Table:

id (INTEGER PRIMARY KEY)

title (TEXT)

author (TEXT)

is_issued (INTEGER DEFAULT 0)

issued_to (INTEGER)

users Table:

id (INTEGER PRIMARY KEY)

name (TEXT)

logs Table:

id (INTEGER PRIMARY KEY AUTOINCREMENT)

action (TEXT)

details (TEXT)

timestamp (DATETIME DEFAULT CURRENT_TIMESTAMP)

License
This project is open-source and available under the MIT License.
