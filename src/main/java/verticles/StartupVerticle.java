package verticles;

import com.google.inject.Guice;
import com.google.inject.Injector;
import guice.ApplicationPropertiesModule;
import guice.CouchbaseServiceModule;
import guice.GuiceVerticleFactory;
import guice.UserServiceModule;
import io.reactivex.Completable;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static constants.Constant.*;

public class StartupVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(StartupVerticle.class);

    @Override
    public Completable rxStart() {
        ConfigStoreOptions configStoreOptions = new ConfigStoreOptions().setType(FILE).setFormat(PROPERTIES).setConfig(new JsonObject().put(PATH, "application.properties"));
        //todo This and verticle deployment could be improved using rxjava
        ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(configStoreOptions)).getConfig(appProp -> {
            if (appProp.succeeded()) {
                createInjectorAndRegister(appProp.result());
            }
        });
        return super.rxStart();
    }

    private void createInjectorAndRegister(JsonObject applicationProperties) {
        Injector injector = Guice.createInjector(new ApplicationPropertiesModule(applicationProperties), new CouchbaseServiceModule(), new UserServiceModule());
        GuiceVerticleFactory guiceVerticleFactory = new GuiceVerticleFactory(injector);
        vertx.registerVerticleFactory(guiceVerticleFactory);
        vertx.deployVerticle("guice:verticles.UserProfileVerticle", deploymentID -> {
            if (deploymentID.succeeded()) {
                logger.info("Successfully deployed verticle id={}", deploymentID.result());
            } else {
                logger.error("Verticle deployment failed | error={}", deploymentID.cause().getMessage());
            }
        });
    }
}
