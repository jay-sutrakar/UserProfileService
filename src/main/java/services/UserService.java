package services;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import models.User;

public class UserService {
    private final UserRepositoryClient userRepositoryClient;

    public UserService(UserRepositoryClient userRepositoryClient) {
        this.userRepositoryClient = userRepositoryClient;
    }

    public Single<JsonObject> register(String userId, User user) {
        return userRepositoryClient.exists(userId).flatMap(res -> Boolean.TRUE.equals(res) ? Single.error(new RuntimeException("User already exist")) : userRepositoryClient.add(user.getUsername(), JsonObject.mapFrom(user)));

    }

    public Single<User> fetchUser(String userId) {
        return userRepositoryClient.exists(userId).flatMap(res -> Boolean.TRUE.equals(res) ? userRepositoryClient.get(userId).map(user -> user.mapTo(User.class)) : Single.error(new RuntimeException("User does not exist exist")));

    }

    public Single<JsonObject> updateUser(String userId, User user) {
        return userRepositoryClient.exists(userId).flatMap(res -> Boolean.TRUE.equals(res) ? userRepositoryClient.update(userId, JsonObject.mapFrom(user)) : Single.error(new RuntimeException("User does not exist exist")));
    }

}
