package app;

import io.javalin.Javalin;
import user.User;
import user.repository.UserRepository;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.*;
import io.micrometer.prometheus.*;

public class App {
    public static void main(String[] args) {
        io.micrometer.core.instrument.Counter userApiCounter = io.micrometer.core.instrument.Metrics.counter("user_api_counter");
        var users = new UserRepository();

        Javalin app = Javalin.create().start(7000);
        app.get("/", (ctx) -> {
            System.out.println("Root Endpoint is requested");
            ctx.result("Root Endpoint is requested");
        });

        app.get("api/users", ctx -> {
            userApiCounter.increment();
        });

        app.get("api/users/{id}", ctx -> {
            var id = Integer.valueOf(ctx.pathParam("id"));
            var user = users.findById(id);
            if (user.isEmpty()) {
                ctx.status(404);
            } else {
                ctx.json(user.get());
            }
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

        // add prometheus metrics endpoint
        app.get("/metrics", ctx -> {
            ctx.result(io.micrometer.prometheus.PrometheusMeterRegistry.DEFAULT.getPrometheusRegistry().scrape());

        });



    }

}
