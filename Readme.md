# 🚀 SpaceGame Client-Server Application

A desktop-based Java application built with **Java Swing**, featuring local data persistence via **SQLite**, and client-server communication with a **[Spring Boot REST API](https://github.com/ihorkryvoruchko/Space-Game-API)** via JSON over HTTP.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Java
- **UI Framework:** Java Swing (`JFrame`, `JPanel`)
- **Networking:** Java `HttpClient` (`java.net.http`), asynchronous requests using `CompletableFuture`
- **JSON Processing:** Jackson (`ObjectMapper`) for serialization/deserialization
- **Local Database:** SQLite JDBC
- **Build Tool:** Maven
- **Architecture Pattern:** Modular layered structure (`model`, `repository`, `view`)

---

## 📂 Project Structure

```text
SpaceGame/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── model/         # Data transfer objects and entity models
│   │   │   ├── repository/    # Local SQLite DAO and Remote REST API clients
│   │   │   ├── view/          # Swing UI components (GameWindow, MenuWindow, GamePanel)
│   │   │   └── Start.java     # Application entry point
│   │   └── resources/         # Static assets (sprites, audio tracks)
│   └── target/                # Build output (excluded from Git)
├── .gitignore
├── pom.xml                    # Maven dependencies configuration
└── README.md