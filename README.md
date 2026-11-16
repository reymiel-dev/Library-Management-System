# Library Management System

A console-based library management system written in Java. It supports separate admin and member accounts, borrowing and returning with fines, and saves all data between runs.

## Features

**Admin**
- Add, edit, remove, search and display library items (books and magazines)
- Register, edit, search, activate/deactivate, reset the password of, and delete members
- View all borrowing records
- View library statistics (items, copies, active and overdue loans)
- Rename the library

**Member**
- View and edit profile, change password
- Browse and search library items
- Borrow and return items
- View borrowing history
- View and pay unpaid fines

## Library Rules

- Loan period: 14 days
- Fine: PHP 2.00 per day overdue
- Borrowing limit: Regular members 3 items, Premium members 5 items
- Members with unpaid fines or inactive accounts cannot borrow
- Items and members with active loans cannot be removed

## Concepts Used

- Object-oriented programming: abstraction, inheritance, encapsulation, polymorphism
- Abstract class (`LibraryItem`), interface (`Borrowable`), enum (`MembershipType`)
- Collections (`ArrayList`)
- File persistence through Java serialization
- Date handling with `java.time`

## Project Structure

```

library_management_system/
├── Main.java            # Menus, login, program entry point
├── Library.java         # Core logic: items, members, loans
├── LibraryItem.java     # Abstract parent of Book and Magazine
├── Book.java
├── Magazine.java
├── Member.java
├── Loan.java
├── MembershipType.java
├── Borrowable.java
├── Constants.java       # Fine rate, loan days, admin login, data file
└── InputHelper.java     # Validated keyboard input
```

## Requirements

- Java JDK 8 or higher
- Any IDE (NetBeans, IntelliJ, Eclipse, VS Code) or the command line

## How to Run

**IDE:** Open the project and run `Main.java`.

**Command line:**
```bash
javac library_management_system/*.java
java library_management_system.Main
```

## Default Admin Login

| Username | Password |
|----------|----------|
| admin    | admin123 |

Members are registered by the admin from the Member Management menu.

## Data Storage

Data is saved automatically to `library_data.ser` in the project folder. On first run, the system creates a sample library with 5 books and 2 magazines. Delete `library_data.ser` to reset all data.

## Notes

This is a school project. Passwords are stored as plain text and the admin login is hardcoded, so it is not meant for real-world use.
