package app;

import io.javalin.Javalin;
import user.User;
import user.repository.UserRepository;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.*;

public class App {
    public static void main(String[] args) {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        registry.config().commonTags("application", "My-Application");

        new ClassLoaderMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new UptimeMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new DiskSpaceMetrics(new File(System.getProperty("user.dir"))).bindTo(registry);

        var users = new UserRepository();

        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new MicrometerPlugin(registry));
        }).start(7000);
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

        app.get("/metrics", ctx -> ctx.contentType(TextFormat.CONTENT_TYPE_004).result(registry.scrape()));


    }

}
