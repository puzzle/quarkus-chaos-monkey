package ch.puzzle.quarkus.chaos.monkey.deployment.devconsole;

import ch.puzzle.quarkus.chaos.monkey.MonkeyManagerSupplier;
import ch.puzzle.quarkus.chaos.monkey.devconsole.ChaosMonkeyDevConsoleRecorder;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.devconsole.spi.DevConsoleRouteBuildItem;
import io.quarkus.devconsole.spi.DevConsoleRuntimeTemplateInfoBuildItem;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

public class DevConsoleProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    public DevConsoleRuntimeTemplateInfoBuildItem collectBeanInfo() {
        return new DevConsoleRuntimeTemplateInfoBuildItem("monkeyInfo", new MonkeyManagerSupplier());
    }

    @BuildStep
    @Record(value = STATIC_INIT, optional = true)
    DevConsoleRouteBuildItem invokeMonkeysEndpoint(ChaosMonkeyDevConsoleRecorder recorder) {
        return new DevConsoleRouteBuildItem("monkeys", "POST", recorder.monkeyHandler());
    }

    @BuildStep
    @Record(value = STATIC_INIT, optional = true)
    DevConsoleRouteBuildItem invokeAnnotationsEndpoint(ChaosMonkeyDevConsoleRecorder recorder) {
        return new DevConsoleRouteBuildItem("annotations", "POST", recorder.annotationHandler());
    }
}
