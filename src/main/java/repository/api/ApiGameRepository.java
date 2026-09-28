package repository.api;

import model.GameModel;
import repository.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ApiGameRepository extends AbstractApiRepository implements GameRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public GameModel save(GameModel game) {
        try {
            String jsonBody = objectMapper.writeValueAsString(game);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.BASE_URL + "/games"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {

                GameModel savedGame = objectMapper.readValue(response.body(), GameModel.class);
                savedGame.setUserModel(game.getUserModel());

                return savedGame;

            } else {
                System.out.println("Server Fehler: " + response.body());
            }

        } catch (Exception e) {
            System.err.println("Client Fehler: " + e.getMessage());
        }

        return null;
    }

    @Override
    public GameModel update(GameModel game) {
        this.updateAsync(game);

        return game;
    }

    public CompletableFuture<GameModel> updateAsync(GameModel game) {
        // Проверяем ID
        if (game.getId() == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Die Spalte id ist obligatorisch!")
            );
        }

        String jsonBody;
        try {

            jsonBody = objectMapper.writeValueAsString(game);

        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.BASE_URL + "/games/" + game.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody)) // PUT-запрос
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            System.out.println("Der Spiel wurde erfolgreich geändert!");

                            return objectMapper.readValue(response.body(), GameModel.class);
                        } catch (Exception e) {
                            System.err.println("Antwort Parsing Fehler: " + e.getMessage());
                            return null;
                        }
                    } else {
                        System.out.println("Server Fehler: " + response.statusCode());
                        return null;
                    }
                });
    }
}
