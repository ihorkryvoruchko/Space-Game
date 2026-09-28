package repository;

import model.UserModel;

public interface UserRepository {

    public UserModel save(UserModel user);

    public UserModel findByName(String name);
}
