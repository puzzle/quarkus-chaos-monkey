package ch.puzzle.quarkus.chaos.monkey.devconsole;

import ch.puzzle.quarkus.chaos.monkey.Monkey;
import ch.puzzle.quarkus.chaos.monkey.MonkeyManager;
import io.quarkus.arc.Arc;
import io.quarkus.devconsole.runtime.spi.DevConsolePostHandler;
import io.quarkus.devconsole.runtime.spi.FlashScopeUtil;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Recorder
public class ChaosMonkeyDevConsoleRecorder {

    private static Logger logger = LoggerFactory.getLogger(ChaosMonkeyDevConsoleRecorder.class);

    public Handler<RoutingContext> monkeyHandler() {
        return new DevConsolePostHandler() {
            @Override
            protected void handlePost(RoutingContext event, MultiMap form) throws Exception {
            String clazzName = form.get("clazzName");
            String methodName = form.get("methodName");

            try {
                boolean enabled = Boolean.parseBoolean(form.get("enabled"));
                boolean throwException = Boolean.parseBoolean(form.get("throwException"));
                double errorRate = Double.parseDouble(form.get("errorRate"));
                long latencyMs = Long.parseLong(form.get("latencyMs"));

                Monkey monkey = Arc.container().instance(MonkeyManager.class).get().get(clazzName, methodName);
                monkey.setEnabled(enabled);
                monkey.setThrowException(throwException);
                monkey.setErrorRate(errorRate);
                monkey.setLatencyMs(latencyMs);

                flashMessage(event, "Chaos Monkey " + clazzName + "-" + methodName + " " + " updated",
                        FlashScopeUtil.FlashMessageStatus.SUCCESS);
            } catch(NumberFormatException e) {
                flashMessage(event, "Failed to update Chaos Monkey " + clazzName + "-" + methodName+": "+e.getMessage(),
                        FlashScopeUtil.FlashMessageStatus.ERROR);
            }
            }
        };
    }

    public Handler<RoutingContext> annotationHandler() {
        return new DevConsolePostHandler() {
            @Override
            protected void handlePost(RoutingContext event, MultiMap form) throws Exception {
            String clazzName = form.get("clazzName");
            String methodName = form.get("methodName");

            try {
                Monkey monkey = new Monkey();
                monkey.setClazzName(clazzName);
                monkey.setMethodName(methodName);
                monkey.setEnabled(false);
                monkey.setThrowException(false);
                monkey.setErrorRate(0.0D);
                monkey.setLatencyMs(0L);

                Arc.container().instance(MonkeyManager.class).get().add(monkey);

                flashMessage(event, "Chaos Monkey " + clazzName + "-" + methodName + " " + " added",
                        FlashScopeUtil.FlashMessageStatus.SUCCESS);
            } catch(NumberFormatException e) {
                flashMessage(event, "Failed to add Chaos Monkey " + clazzName + "-" + methodName+": "+e.getMessage(),
                        FlashScopeUtil.FlashMessageStatus.ERROR);
            }
            }
        };
    }
}
