package verticles;

import handlers.RequestHandler;
import io.reactivex.Completable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class UserProfileVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        RequestHandler requestHandler = new RequestHandler(null);
        Router router = getRouter(requestHandler);

        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(8081)
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
