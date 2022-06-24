package services;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

public interface UserRepositoryClient {
    Single<JsonObject> add(String id, JsonObject content);

    Single<JsonObject> delete(String id);

    Single<JsonObject> update(String id, JsonObject content);

    Single<JsonObject> get(String id);

    Single<Boolean> exists(String id);
}
