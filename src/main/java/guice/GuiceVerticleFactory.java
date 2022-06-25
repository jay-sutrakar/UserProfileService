package guice;

import com.google.inject.Injector;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;

import java.util.concurrent.Callable;

public class GuiceVerticleFactory implements VerticleFactory {
    private final Injector injector;

    public GuiceVerticleFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public String prefix() {
        return "guice";
    }

    @Override
    public void createVerticle(String verticleName, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {
        String name = VerticleFactory.removePrefix(verticleName);
        promise.complete(() -> (Verticle) injector.getInstance(Class.forName(name)));
    }
}
