package ch.puzzle.quarkus.chaos.monkey;

import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Recorder
public class ChaosMonkeyRecorder {

    public Supplier<Object> createContext(List<ChaosMonkeyMethodMetadata> chaosMonkeyMethods) {
        return new Supplier<Object>() {
            @Override
            public Object get() {
                return new ChaosMonkeyAnnotatedMethods() {
                    @Override
                    public List<ChaosMonkeyMethodMetadata> getChaosMonkeyMethods() {
                        return chaosMonkeyMethods;
                    }
                };
            }
        };
    }

    public Handler<RoutingContext> chaosMonkeyHandler() {
        return new ChaosMonkeyHandler();
    }

    public Function<Router, Route> routeFunction(String rootPath, Handler<RoutingContext> bodyHandler, ChaosMonkeyRuntimeConfig runtimeConfig) {

        if (runtimeConfig.enable) {
            return new Function<Router, Route>() {
                @Override
                public Route apply(Router router) {
                    String pathRegexp = rootPath.replace("/", "\\/");
                    return router.route(rootPath).pathRegex(pathRegexp+"(\\/(?<id>[^\\/]+))?").handler(bodyHandler);
                }
            };
        } else {
            return new Function<Router, Route>() {
                @Override
                public Route apply(Router router) {
                    return router.route(rootPath).handler(new ChaosMonkeyNotFoundHandler());
                }
            };
        }
    }

    public Supplier<MonkeyManager> getChaosMonkeySupplier() {
        return MonkeyManager::new;
    }
}
