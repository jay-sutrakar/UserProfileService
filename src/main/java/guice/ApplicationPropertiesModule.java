package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationPropertiesModule extends AbstractModule {
    private final JsonObject applicationProperties;

    @Provides
    @Singleton
    public JsonObject providesApplicationProperties() {
        return applicationProperties;
    }
}
