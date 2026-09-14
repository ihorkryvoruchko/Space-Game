package repository.sqlite;

import model.UserModel;
import repository.UserRepository;
import java.sql.*;


public class SqliteUserRepository extends AbstractSqliteRepository implements UserRepository {


    public SqliteUserRepository() {
        super();
    }


    @Override
    public UserModel save(UserModel user) {
        UserModel userModel = this.findByName(user.getName());
        if (userModel != null) {
            return userModel;
        }

        String sql = "INSERT INTO user (name) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    UserModel newUser = new UserModel();
                    newUser.setId(newId);
                    newUser.setName(user.getName());
                    // Возвращаем НОВЫЙ объект User уже с присвоенным id из БД
                    return newUser;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения пользователя: " + user.getName(), e);
        }

        throw new RuntimeException("Не удалось сохранить пользователя");
    }

    @Override
    public UserModel findByName(String name) {
        String sql = "SELECT id, name FROM user WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserModel user = new UserModel();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));

                    return user;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске пользователя по name: " + name, e);
        }

        return null;
    }


}