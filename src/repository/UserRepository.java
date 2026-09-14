package repository;

import model.UserModel;
import java.util.Optional;

public interface UserRepository {

    public UserModel save(UserModel user);

    public UserModel findByName(String name);
}
