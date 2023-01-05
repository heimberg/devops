package app;

import io.javalin.Javalin;
import user.User;
import user.repository.UserRepository;
import io.micrometer.prometheus.*;

public class App {
    public static void main(String[] args) {
        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        io.micrometer.core.instrument.Counter userApiCounter = prometheusRegistry.counter("user_api_counter");
        io.micrometer.core.instrument.Counter getUserByIDCounter = prometheusRegistry.counter("get_user_by_id_counter");
        io.micrometer.core.instrument.Counter postNewUserCounter = prometheusRegistry.counter("post_new_user_counter");
        io.micrometer.core.instrument.Counter deleteUserCounter = prometheusRegistry.counter("delete_user_counter");

        var users = new UserRepository();

        Javalin app = Javalin.create().start(7000);
        app.get("/", (ctx) -> {
            System.out.println("Root Endpoint is requested");
            ctx.result("Root Endpoint is requested");
        });

        app.get("api/users", ctx -> {
            userApiCounter.increment();
            ctx.json(users.findAll());
        });

        app.get("api/users/{id}", ctx -> {
            var id = Integer.valueOf(ctx.pathParam("id"));
            var user = users.findById(id);
            if (user.isEmpty()) {
                ctx.status(404);
            } else {
                getUserByIDCounter.increment();
                ctx.json(user.get());
            }
        });

        app.delete("api/users/{id}", ctx -> {
            deleteUserCounter.increment();
            var id = Integer.valueOf(ctx.pathParam("id"));
            users.delete(id);
            ctx.status(204);
        });

        app.post("api/users", ctx -> {
            postNewUserCounter.increment();
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

        // add prometheus metrics endpoint
        app.get("/metrics", ctx -> {

            ctx.result(prometheusRegistry.scrape());
        });



    }

}
