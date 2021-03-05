package ch.puzzle.quarkus.chaos.monkey;

import io.quarkus.arc.Arc;
import io.quarkus.vertx.http.runtime.devmode.Json;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

import static io.vertx.core.http.HttpMethod.*;

public class ChaosMonkeyHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        HttpServerRequest request = ctx.request();
        HttpServerResponse response = ctx.response();

        HttpMethod method = request.method();

        if (GET == method) {
            handleGet(request, response);
        } else if (POST == method) {
            handlePost(request, response, ctx);
        } else if (PUT == method) {
            handlePut(request, response, ctx);
        } else if (DELETE == method) {
            handleDelete(request, response);
        }
    }

    private void handleGet(HttpServerRequest request, HttpServerResponse response) {
        response.headers().add("Content-Type", "application/json");
        String id = request.getParam("id");

        if (id == null || id.isEmpty()) {
            response.end(toJson(getMonkeyManager().getAll()).build());
        } else {
            response.end(toJson(id, getMonkeyManager().get(id)).build());
        }
    }

    private void handlePost(HttpServerRequest request, HttpServerResponse response, RoutingContext ctx) {
        ctx.getBodyAsJsonArray().forEach(monkey -> getMonkeyManager().add(toMonkey((JsonObject)monkey)));
        response.setStatusCode(204).end();
    }

    void handlePut(HttpServerRequest request, HttpServerResponse response, RoutingContext ctx) {
        String id = request.getParam("id");

        if (id == null || id.isEmpty()) {
            response.setStatusCode(404).end();
        } else {
            getMonkeyManager().addMonkey(toMonkey(ctx.getBodyAsJson()), id);
            response.setStatusCode(204).end();
        }
    }

    void handleDelete(HttpServerRequest request, HttpServerResponse response) {
        String id = request.getParam("id");

        if (id == null || id.isEmpty()) {
            response.setStatusCode(404).end();
        } else {
            getMonkeyManager().removeMonkey(id);
            response.setStatusCode(204).end();
        }
    }

    Json.JsonArrayBuilder toJson(Map<String, Monkey> monkeys) {
        Json.JsonArrayBuilder jab = Json.array();

        if (monkeys != null) {
            monkeys.forEach((key, value) -> jab.add(toJson(key, value)));
        }

        return jab;
    }


    Json.JsonObjectBuilder toJson(String id, Monkey monkey) {
        Json.JsonObjectBuilder job = Json.object();

        if (id != null && !id.isEmpty()) {
            job.put("id", id)
                    .put("clazzName", monkey.getClazzName())
                    .put("methodName", monkey.getMethodName())
                    .put("enabled", monkey.isEnabled())
                    .put("throwException", monkey.isThrowException())
                    .put("errorRate", String.valueOf(monkey.getErrorRate()))
                    .put("latencyMs", monkey.getLatencyMs());
        }

        return job;
    }

    Monkey toMonkey(JsonObject obj) {
        Monkey m = new Monkey();
        if(obj != null) {
            m.setClazzName(obj.getString("clazzName"));
            m.setMethodName(obj.getString("methodName"));
            m.setEnabled(obj.getBoolean("enabled"));
            m.setThrowException(obj.getBoolean("throwException"));
            m.setErrorRate(obj.getDouble("errorRate"));
            m.setLatencyMs(obj.getLong("latencyMs"));
        }

        return m;
    }

    MonkeyManager getMonkeyManager() {
        return Arc.container().instance(MonkeyManager.class).get();
    }
}
