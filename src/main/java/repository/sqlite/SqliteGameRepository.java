package repository.sqlite;

import model.GameModel;
import repository.GameRepository;
import java.sql.*;

public class SqliteGameRepository extends AbstractSqliteRepository implements GameRepository {


    public SqliteGameRepository() {
        super();
    }

    @Override
    public GameModel save(GameModel game) {
        String sql = "INSERT INTO game (user_id, score) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, game.getUserModel().getId());
            pstmt.setInt(2, game.getScore());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    GameModel newGame = new GameModel();
                    newGame.setId(newId);
                    newGame.setScore(game.getScore());
                    newGame.setUserModel(game.getUserModel());

                    return newGame;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения game: " + game.getUserModel().getName(), e);
        }

        throw new RuntimeException("Не удалось сохранить game");
    }

    @Override
    public GameModel update(GameModel game) {
        String sql = "UPDATE game SET score = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, game.getScore());
            pstmt.setLong(2, game.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {

                return game;
            } else {
                System.err.println("Game " + game.getId() + " not found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fehler: " + game.getId(), e);
        }

        return null;
    }


}