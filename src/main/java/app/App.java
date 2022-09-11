package app;

import io.javalin.Javalin;
import user.api.UserController;
import user.repository.UserRepository;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.get;

public class App {
    public static void main(String[] args) {
        var users = new UserRepository();

        Javalin app = Javalin.create().start(7000);
        app.routes(() -> {
            UserController usersHandler = new UserController(users);

            crud("/users/{user-id}", usersHandler);

            get("/users/email/{email}", ctx ->
                    usersHandler.findByEmail(ctx, ctx.pathParam("email")));
            get("/users/birthYear/{birthYear}", ctx ->
                    usersHandler.findByBirthYear(ctx, Integer.valueOf(ctx.pathParam("birthYear"))));
        });

        app.get("/", ctx -> ctx.result("Hello World"));
    }

}
