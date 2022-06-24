import io.vertx.reactivex.core.Vertx;
import verticles.UserProfileVerticle;

public class Application {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new UserProfileVerticle());
    }
}
