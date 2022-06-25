import io.vertx.reactivex.core.Vertx;
import verticles.StartupVerticle;

public class Application {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(new StartupVerticle()).subscribe();
    }
}
