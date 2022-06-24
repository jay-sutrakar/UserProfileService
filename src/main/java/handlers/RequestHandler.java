package handlers;

import services.UserService;

public class RequestHandler {

    private final UserService userService;
    RequestHandler(UserService userService) {
        this.userService = userService;
    }
}
