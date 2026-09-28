package repository.api;

import model.UserModel;
import repository.UserRepository;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ApiUserRepository extends AbstractApiRepository implements UserRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiUserRepository() {
        super();
    }

    @Override
    public UserModel save(UserModel user) {
        try {
            UserModel playUser = this.findByName(user.getName());

            if (playUser != null) {
                return playUser;
            }

            String jsonBody = objectMapper.writeValueAsString(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.BASE_URL + "/users"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {

                return objectMapper.readValue(response.body(), UserModel.class);

            } else {
                System.out.println("Server Fehler: " + response.body());
            }

        } catch (Exception e) {
            System.err.println("Client Fehler: " + e.getMessage());
        }

        return null;
    }

    @Override
    public UserModel findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Die Nahme kann nicht leer sein!");
            return null;
        }

        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.BASE_URL + "/users/name/" + encodedName))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("User wurde erfolgreich gefunden!");

                return objectMapper.readValue(response.body(), UserModel.class);

            } else if (response.statusCode() == 404) {
                System.err.println("Der User wurde nicht mit Name '" + name + "' gefunden.");
            } else {
                System.err.println("Der Server Fehler: " + response.statusCode() + " -> " + response.body());
            }

        } catch (Exception e) {
            System.err.println("Client Fehler: " + e.getMessage());
        }

        return null;
    }
}
