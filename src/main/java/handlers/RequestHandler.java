package handlers;

import exception.UserServiceException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.ext.web.RoutingContext;
import models.User;
import services.UserService;

import java.util.Objects;
import java.util.regex.Pattern;

import static constants.Constant.USERNAME;

public class RequestHandler {

    private final Pattern validUserName = Pattern.compile("");
    private final Pattern validEmail = Pattern.compile("");

    private final UserService userService;

    public RequestHandler(UserService userService) {
        this.userService = userService;
    }

    public void validateRegistration(RoutingContext rc) {
        User user = rc.body().asPojo(User.class);
        try {
            Objects.requireNonNull(user.getUsername(), "Username can not be empty !");
            Objects.requireNonNull(user.getEmail(), "Email can not be empty !");
            Objects.requireNonNull(user.getCity(), "City can not be empty");
            Objects.requireNonNull(user.getPassword(), "Password can not be empty");
            Objects.requireNonNull(user.getDeviceId(), "DeviceId can not be empty");
        } catch (NullPointerException e) {
            rc.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(e.getMessage());
        }
        if (!validEmail.matcher(user.getEmail()).matches()) {
            rc.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end("Invalid email format");
        }
        rc.next();
    }

    public void register(RoutingContext rc) {
        User user = rc.body().asPojo(User.class);
        userService.registerUser(user.getUsername(), user).subscribe(res -> {
            rc.response().setStatusCode(HttpResponseStatus.OK.code()).end("Registration completed");
        }, error -> handleError(rc, error));
    }

    public void fetch(RoutingContext rc) {
        String userId = rc.request().getParam(USERNAME);
        userService.fetchUser(userId).subscribe(user -> rc.response().setStatusCode(HttpResponseStatus.OK.code()).end(user.toString()), error -> handleError(rc, error));
    }

    public void update(RoutingContext rc) {
        String userId = rc.request().getParam(USERNAME);
        User user = rc.body().asPojo(User.class);
        userService.updateUser(userId, user).subscribe(res -> rc.response().setStatusCode(HttpResponseStatus.OK.code()).end("Update completed"), error -> handleError(rc, error));
    }

    public void authenticate(RoutingContext rc) {

    }

    public void whoOwns(RoutingContext rc) {
        String deviceId = rc.request().getParam("deviceId");
        String query = String.format("select `users`.* from `document`.`user_profile_service`.`users` where `users`.`deviceId` = %s", deviceId);
        userService.executeQuery(query).subscribe(res -> rc.response().setStatusCode(HttpResponseStatus.OK.code()).end(res.toString()), error -> handleError(rc, error));
    }


    private void handleError(RoutingContext rc, Throwable error) {
        if (error instanceof UserServiceException) {
            rc.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(error.getMessage());
        } else {
            rc.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end(error.getMessage());
        }
    }
}
