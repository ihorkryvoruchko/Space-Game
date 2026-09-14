package repository;

import model.GameModel;

public interface GameRepository {

    public GameModel save(GameModel game);

    public GameModel update(GameModel game);

}
