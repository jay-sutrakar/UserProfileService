package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.vertx.core.json.JsonObject;
import services.CouchbaseService;


public class CouchbaseServiceModule extends AbstractModule {
    @Provides
    @Singleton
    public CouchbaseService provideCouchbaseService(JsonObject applicationProperties) {
        return new CouchbaseService(applicationProperties);
    }
}
