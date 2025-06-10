# >Date = 5/24/2025
Main : Set-up project Java with Maven in VSCode

## Table of Contents
1.  [Project Overview](#project-overview)
2.  [Features](#features)
3.  [Technology Stack](#technology-stack)
4.  [Set-up and Install](#set-up-and-install)
5.  [Project Structure](#project-structure)
6.  [Start Java Maven Project](#start-java-maven-project)
7.  [Build and Run the Project](#build-and-run-the-project)
8.  [Database Setup (MySQL with PHPMyAdmin)](#database-setup-mysql-with-phpmyadmin)
9.  [JavaFX Setup](#javafx-setup)
10. [Scene Builder Setup](#scene-builder-setup-in-java)
11. [Application Flow](#application-flow)
    * [User Registration](#user-registration)
    * [User Login](#user-login)
    * [Home Page](#home-page)
    * [Daily Activity Feature](#daily-activity-feature)
    * [E-Commerce Feature (Placeholder)](#e-commerce-feature-placeholder)
12. [Styling](#styling)

---

## Project Overview
MyJavaApp is a desktop application built using JavaFX and Maven. It aims to provide users with features like daily activity tracking and e-commerce (planned). The application uses a MySQL database for data persistence.

---

## Features
* User registration and login with role-based access (ADMIN, USER).
* Home page dashboard for navigation.
* **Daily Activity Management:**
    * Add new to-do items with category, description, date, and time.
    * View a list of all to-do items.
    * Mark to-do items as completed.
    * Delete to-do items.
    * Data persisted in a MySQL database.
* E-Commerce section (currently a placeholder, navigates from Home).

---

## Technology Stack
* **Programming Language:** Java 17
* **Framework:** JavaFX 21 for UI
* **Build Tool:** Apache Maven
* **Database:** MySQL (managed via XAMPP/PHPMyAdmin)
* **IDE:** VSCode with Java Extension Pack
* **Libraries:**
    * JUnit 3.8.1 (for testing)
    * FontAwesomeFX 4.7.0-9.1.2 (for icons)
    * MySQL Connector/J 8.0.33 (JDBC driver)

---

## Set-up and Install
1.  **Java JDK (version 17 or compatible):** Install and set up the `JAVA_HOME` environment variable.
2.  **Maven:** Install and set up the `MAVEN_HOME` environment variable and add Maven's `bin` directory to your system's PATH.
3.  **VSCode:** Install Visual Studio Code.
4.  **VSCode Extensions:** Install the "Extension Pack for Java" from the VSCode Marketplace.
5.  **XAMPP:** Install XAMPP for Apache and MySQL database server.
6.  **MySQL Connector/J:** Ensure the dependency is included in `pom.xml`.
7.  **JavaFX Dependencies:** Ensure `javafx-controls` and `javafx-fxml` dependencies are in `pom.xml`.

---

## Latest Project Structure (5/24/2025)
MyJavaApp/
├── pom.xml                # Maven Project Object Model file
├── README.md              # This file
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── app/
│   │   │               ├── MainApp.java        # Main application class, entry point
│   │   │               ├── LoginController.java  # Controller for login.fxml
│   │   │               ├── RegisterController.java # Controller for register.fxml
│   │   │               ├── HomeController.java   # Controller for home.fxml
│   │   │               ├── DailyActivityController.java # Controller for dailyactivity.fxml
│   │   │               ├── EcommerceController.java # Controller for ecommerce.fxml
│   │   │               ├── model/              # Data model classes
│   │   │               │   ├── User.java       # User data model
│   │   │               │   ├── Role.java       # Enum for User roles (ADMIN, USER)
│   │   │               │   └── ToDoItem.java   # To-do item data model
│   │   │               ├── dao/                # Data Access Object interfaces and implementations
│   │   │               │   ├── ToDoDAO.java    # Interface for ToDoItem data access
│   │   │               │   └── ToDoDAOImpl.java # Implementation for ToDoItem data access
│   │   │               ├── service/            # Service layer classes
│   │   │               │   ├── UserService.java  # Handles user-related business logic
│   │   │               │   └── ToDoService.java  # Handles to-do related business logic (currently basic)
│   │   │               └── db/                 # Database utility classes
│   │   │                   └── DBUtil.java     # Utility for database connections
│   │   └── resources/         # Resource files (FXML, CSS, images, etc.)
│   │       ├── login.fxml
│   │       ├── register.fxml
│   │       ├── home.fxml
│   │       ├── dailyactivity.fxml
│   │       ├── ecommerce.fxml
│   │       └── css/
│   │           └── style.css        # Main CSS stylesheet
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── app/
│                       └── AppTest.java    # Unit tests
└── target/                    # Compiled output and packaged JAR
└── classes/
└── css/
└── style.css      # Copied CSS file during build (ensure your build process handles this)

---

## Start Java Maven Project
1.  **Create Project from Archetype:** Open your terminal or command prompt and run:
    ```bash
    mvn archetype:generate -DgroupId=com.example.app -DartifactId=MyJavaApp -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
    ```
    * `groupId`: Typically your organization's domain (e.g., `com.example.app`).
    * `artifactId`: Your project's name (e.g., `MyJavaApp`).
    This command creates a folder named `MyJavaApp` with the standard Maven directory structure.
2.  **Initial Structure:** After generation, the directory structure will be basic. You'll need to add FXML files, controllers, and other packages as shown in the [Project Structure](#project-structure) section.

---

## Build and Run the Project
1.  **Build:** Open the terminal in the root directory of `MyJavaApp` and run:
    ```bash
    mvn clean package
    ```
    This command compiles the code, runs tests, and packages the application into a JAR file in the `target/` directory.
2.  **Run:** After a successful build, you can run the application. The `pom.xml` is configured with `javafx-maven-plugin` to specify the main class (`com.example.app.MainApp`).
    ```bash
    mvn javafx:run
    ```
    Alternatively, if your IDE is set up correctly with Maven, you can often run the `MainApp.java` class directly from the IDE.

    **Ensure `pom.xml` includes the `javafx-maven-plugin` for running:**
    ```xml
    <plugin>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-maven-plugin</artifactId>
        <version>0.0.8</version> <configuration>
            <mainClass>com.example.app.MainApp</mainClass>
        </configuration>
    </plugin>
    ```
    And the `maven-compiler-plugin` for setting Java version:
    ```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.11.0</version> <configuration>
            <source>17</source>
            <target>17</target>
        </configuration>
    </plugin>
    ```

---

## Database Setup (MySQL with PHPMyAdmin)
The application uses a MySQL database named `edulife`.

1.  **Install XAMPP:** Download and install XAMPP.
2.  **Start Services:** Open XAMPP Control Panel and start the **Apache** and **MySQL** modules.
3.  **Open PHPMyAdmin:** Navigate to `http://localhost/phpmyadmin` in your web browser.
4.  **Create Database:**
    * Click on "New" on the left sidebar.
    * Enter `edulife` as the database name.
    * Choose a collation (e.g., `utf8mb4_general_ci`) and click "Create".
5.  **Create `users` Table:**
    * Select the `edulife` database.
    * Go to the "SQL" tab and execute the following SQL script:
      ```sql
      CREATE TABLE users (
          id INT AUTO_INCREMENT PRIMARY KEY,
          username VARCHAR(255) NOT NULL UNIQUE,
          password VARCHAR(255) NOT NULL,
          role VARCHAR(50) NOT NULL -- 'admin' or 'user'
      );
      ```
    * The first user registered will automatically be assigned the `ADMIN` role. Subsequent users will be `USER`.
6.  **Create `todo_items` Table:**
    * Select the `edulife` database.
    * Go to the "SQL" tab and execute the following SQL script:
      ```sql
      CREATE TABLE todo_items (
          id INT AUTO_INCREMENT PRIMARY KEY,
          user_id INT, -- Optional: if you want to associate tasks with users
          kategori VARCHAR(255) NOT NULL,
          deskripsi TEXT NOT NULL,
          tanggal DATE NOT NULL,
          waktu TIME NOT NULL,
          status BOOLEAN NOT NULL DEFAULT FALSE,
          FOREIGN KEY (user_id) REFERENCES users(id) -- Optional: add if user_id is used
      );
      ```
      *(Note: The current `ToDoItem.java` model and `ToDoDAOImpl.java` do not explicitly use `user_id` in the `todo_items` table for relating tasks to specific users, but it's a common extension. The `addToDo` method in `ToDoDAOImpl` inserts only kategori, deskripsi, tanggal, waktu, status).*
7.  **Database Configuration (`DBUtil.java`):**
    The `src/main/java/com/example/app/db/DBUtil.java` file contains the database connection details.
    * **URL:** `jdbc:mysql://localhost:3306/edulife`
    * **User:** `root`
    * **Password:** `""` (empty password, default for XAMPP)
    Modify these if your local MySQL setup is different.

---

## JavaFX Setup
1.  **Dependencies in `pom.xml`:** Ensure your `pom.xml` includes the necessary JavaFX dependencies. The version should ideally match or be compatible with your JDK version (e.g., JavaFX 21 for JDK 17/21).
    ```xml
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <javafx.version>21</javafx.version> </properties>

    <dependencies>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        </dependencies>
    ```
2.  **FXML Files and Controllers:**
    * Place your FXML files (e.g., `login.fxml`, `home.fxml`, `dailyactivity.fxml`) in the `src/main/resources/` directory (or subdirectories).
    * Controller classes (e.g., `LoginController.java`, `HomeController.java`) should be in your Java source packages (e.g., `com.example.app`).
    * Link FXML files to their controllers using the `fx:controller` attribute in the FXML root element.
3.  **Main Application Class (`MainApp.java`):**
    Your `MainApp.java` should extend `javafx.application.Application` and load the initial FXML file (e.g., `login.fxml`) in its `start()` method.
4.  **CSS Styling:** You can use CSS to style your JavaFX application. Place your CSS files (e.g., `style.css`) in `src/main/resources/css/` and link them in your FXML files or programmatically.

---

## Scene Builder Setup in Java
1.  **Install Scene Builder:** Download and install a standalone Scene Builder application compatible with your JavaFX version.
2.  **Install VSCode Extension (Optional but Recommended):**
    * Search for "SceneBuilder Extension" (often by Gluon) in the VS Code Marketplace and install it.
3.  **Configure Scene Builder Path in VSCode:**
    * Open the Command Palette (`Ctrl+Shift+P` or `Cmd+Shift+P`).
    * Type "JavaFX: Configure Scene Builder path" (the exact command might vary based on the extension) and select it.
    * Browse to and select the executable file of your installed Scene Builder.
4.  **Open FXML Files in Scene Builder from VSCode:**
    * Right-click on an FXML file in the VSCode Explorer.
    * Select "Open in Scene Builder" (or a similar option provided by the extension).

---

## Application Flow

### User Registration
1.  From the Login screen, the user can click the "Register" button/link.
2.  This navigates to the `register.fxml` view, controlled by `RegisterController.java`.
3.  The user enters a desired username and password.
4.  Upon submission:
    * `UserService.isUsernameTaken()` checks if the username already exists in the `users` table.
    * `UserService.determineInitialRole()` determines the role: `ADMIN` if it's the first user, `USER` otherwise.
    * `UserService.registerUser()` inserts the new user with the determined role into the database.
5.  On success, an information alert is shown, and the user is redirected to the Login screen.
6.  Error alerts are shown for issues like incomplete input or username already taken.

### User Login
1.  The application starts with `login.fxml`, controlled by `LoginController.java`.
2.  The user enters their username and password.
3.  Upon clicking "Login":
    * `UserService.loginUser()` validates the credentials against the `users` table in the database.
    * If credentials are valid, a `User` object (containing ID, username, and role) is returned.
    * The `currentLoggedInUser` is set in `LoginController`.
4.  On successful login:
    * An information alert is displayed.
    * The application navigates to `home.fxml`.
    * The logged-in `User` object is passed to `HomeController` to initialize user-specific data/views.
5.  Error alerts are shown for invalid credentials or database issues.

### Home Page
1.  `home.fxml` is controlled by `HomeController.java`.
2.  It displays a welcome message including the username and role of the logged-in user.
3.  Provides navigation buttons/links to other features:
    * **E-Commerce:** Navigates to `ecommerce.fxml` (controlled by `EcommerceController.java`).
    * **Daily Activity:** Navigates to `dailyactivity.fxml` (controlled by `DailyActivityController.java`).
    * **Logout:** Clears the current user session and navigates back to `login.fxml`.

### Daily Activity Feature
1.  Accessed from the Home page, loads `dailyactivity.fxml` controlled by `DailyActivityController.java`.
2.  **Interface:**
    * **Input Form:**
        * `ComboBox` for "Kategori" (Category) with predefined options (Pekerjaan, Belajar, Olahraga, Pribadi, Lainnya).
        * `TextArea` for "Deskripsi" (Description).
        * `DatePicker` for "Tanggal" (Date).
        * `TextField` for "Waktu" (Time) - expects HH:mm format.
        * "Tambah Target" (Add Target) button.
    * **To-Do Table (`TableView`):**
        * Displays existing to-do items with columns: Kategori, Deskripsi, Tanggal, Waktu, Status (CheckBox).
        * The "Status" column uses `CheckBoxTableCell` allowing direct updates.
    * "Hapus Target" (Delete Target) button (enabled when a to-do is selected).
    * "Kembali" (Back) button to navigate to the Home page.
3.  **Functionality:**
    * **Load Data:** On initialization, `muatDataToDo()` fetches all to-do items from the `todo_items` table via `ToDoDAOImpl.getAllToDos()` and populates the `TableView`.
    * **Add Target:**
        * Validates input fields (category selected, all fields filled, correct time format).
        * Creates a `ToDoItem` object.
        * Calls `todoDAO.addToDo()` to save the item to the database.
        * Refreshes the table and clears input fields.
    * **Update Status:**
        * Clicking the checkbox in the "Status" column directly updates the `status` property of the `ToDoItem`.
        * `todoDAO.updateStatus()` is called to persist this change in the database.
    * **Delete Target:**
        * Removes the selected `ToDoItem` from the database using `todoDAO.deleteToDo()`.
        * Refreshes the table.
    * **Navigation:** The "Kembali" button loads `home.fxml`.
4.  **Data Handling:**
    * `ToDoItem.java`: Model class for to-do entries.
    * `ToDoDAO.java` & `ToDoDAOImpl.java`: Interface and implementation for database operations (CRUD) on `todo_items` table.
    * Uses `DBUtil.java` for database connections.
    * Error handling with `Alert` dialogs for database issues or validation failures.

### E-Commerce Feature (Placeholder)
1.  Accessed from the Home page, loads `ecommerce.fxml` controlled by `EcommerceController.java`.
2.  Currently, this section is a placeholder and primarily contains a "Back" button to return to the Home page.

---

## Styling
* The application uses CSS for styling. The main stylesheet is located at `src/main/resources/css/style.css`.
* This file is also found at `MyJavaApp/target/classes/css/style.css` after the project is built.
* FXML files can reference this stylesheet. For example:
    ```xml
    <AnchorPane stylesheets="@/css/style.css" ...>
        </AnchorPane>
    ```
* The `style.css` file includes various style classes for UI elements like buttons (`.login-button`, `.main-button`, `.button-primary`, `.button-danger`), input fields (`.input-field`, `.form-input`), labels (`.title-label`), containers (`.input-form-container`), tables (`.todo-table`), and specific styles for authentication forms (`.auth-form-container`, `.auth-input-field`).