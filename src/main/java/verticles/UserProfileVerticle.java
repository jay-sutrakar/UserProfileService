package verticles;

import com.google.inject.Inject;
import handlers.RequestHandler;
import io.reactivex.Completable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.UserService;

public class UserProfileVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileVerticle.class);
    private final UserService userService;

    @Inject
    UserProfileVerticle(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Completable rxStart() {
        RequestHandler requestHandler = new RequestHandler(userService);
        Router router = getRouter(requestHandler);

        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(8081)
                .doOnSuccess(httpServer -> {
                    logger.info("Server is running on port : {}", httpServer.actualPort());
                })
                .ignoreElement();
    }

    private Router getRouter(RequestHandler requestHandler) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/register").handler(requestHandler::validateRegistration).handler(requestHandler::register);
        router.get("/:username").handler(requestHandler::fetch);
        router.put("/:username").handler(requestHandler::update);
        router.post("/authenticate").handler(requestHandler::authenticate);
        router.get("/owns/:deviceId").handler(requestHandler::whoOwns);
        return router;
    }
}
