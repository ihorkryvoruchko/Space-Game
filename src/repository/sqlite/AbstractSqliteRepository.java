package repository.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractSqliteRepository {
    private final String url;

    public AbstractSqliteRepository() {
        this.url = "jdbc:sqlite:game_data.db";
        this.initDatabase();
    }

    /**
     * Создает и возвращает активное соединение с базой данных SQLite.
     * Отмечаем protected, чтобы дочерние репозитории могли использовать этот метод.
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void initDatabase() {
        String tableUserSql = """
            CREATE TABLE IF NOT EXISTS user (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
            """;

        String tableGameSql = """
            CREATE TABLE IF NOT EXISTS game (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                score INTEGER NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
            );
            """;

        String createIndexSql = """
            CREATE INDEX IF NOT EXISTS idx_game_user_id ON game(user_id);
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Включаем поддержку Foreign Keys для SQLite в этом соединении
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Выполняем создание таблиц и индексов
            stmt.execute(tableUserSql);
            stmt.execute(tableGameSql);
            stmt.execute(createIndexSql);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при инициализации базы данных SQLite", e);
        }
    }
}
