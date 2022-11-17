package app;

import io.javalin.Javalin;
import user.User;
import user.repository.UserRepository;

public class App {
    public static void main(String[] args) {
        var users = new UserRepository();

        Javalin app = Javalin.create().start(7000);
        app.get("/", (ctx) -> {
            System.out.println("Root Endpoint is requested");
            ctx.result("Root Endpoint is requested");
        });

        app.get("api/users", ctx -> {
            ctx.json(users.findAll());
        });

        app.get("api/users/{id}", ctx -> {
            var id = Integer.valueOf(ctx.pathParam("id"));
            var user = users.findById(id);
            user.map(ctx::json);
        });

        app.delete("api/users/{id}", ctx -> {
            var id = Integer.valueOf(ctx.pathParam("id"));
            users.delete(id);
            ctx.status(204);
        });

        app.post("api/users", ctx -> {
            var user = ctx.bodyAsClass(User.class);
            var newUser = users.save(user);
            ctx.json(newUser);
            ctx.status(201);
        });

        app.put("api/users/{id}", ctx -> {
            var id = Integer.valueOf(ctx.pathParam("id"));
            var user = ctx.bodyAsClass(User.class);
            var updatedUser = users.update(id, user);
            ctx.json(users.update(id, user));
            ctx.json(updatedUser);
        });
    }

}
