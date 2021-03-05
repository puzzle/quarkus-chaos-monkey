package ch.puzzle.quarkus.chaos.monkey.deployment;

import ch.puzzle.quarkus.chaos.monkey.*;
import io.quarkus.arc.deployment.*;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.vertx.http.deployment.BodyHandlerBuildItem;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.devmode.NotFoundPageDisplayableEndpointBuildItem;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.jboss.jandex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.jandex.AnnotationTarget.Kind.METHOD;

import java.util.List;
import java.util.stream.Collectors;

class ChaosMonkeyProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkeyProcessor.class);
    private static final String FEATURE = "chaos-monkey";
    static final DotName CHAOSMONKEY_NAME = DotName.createSimple(ChaosMonkey.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerAdditionalBeans(ChaosMonkeyConfig chaosMonkeyConfig,
                                 LaunchModeBuildItem launchMode,
                                 BuildProducer<AdditionalBeanBuildItem> additionalBean) {

        if (shouldInclude(launchMode, chaosMonkeyConfig)) {
            // Create and keep some basic Providers
            additionalBean.produce(AdditionalBeanBuildItem.builder()
                    .setUnremovable()
                    .addBeanClass(MonkeyManager.class)
                    .build());

            // Add annotations and associated interceptors
            additionalBean.produce(AdditionalBeanBuildItem.builder()
                    .addBeanClass(ChaosMonkey.class)
                    .addBeanClass(ChaosMonkeyInterceptor.class)
                    .build());
        }
    }

    @BuildStep
    void collectMonkeys(CombinedIndexBuildItem index, BuildProducer<ChaosMonkeyBusinessMethodItem> monkeyMethods) {

        for (AnnotationInstance monkey : index.getIndex().getAnnotations(CHAOSMONKEY_NAME)) {
            AnnotationTarget annotationTarget = monkey.target();
            if (METHOD.equals(annotationTarget.kind())) {
                MethodInfo mi = ((MethodInfo)monkey.target());
                LOGGER.info("Found @ChaosMonkey Method '"+mi.name()+"' in class '"+mi.declaringClass().simpleName()+"'");
                monkeyMethods.produce(new ChaosMonkeyBusinessMethodItem(mi.declaringClass(), annotationTarget.asMethod()));
            }
        }
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void build(List<ChaosMonkeyBusinessMethodItem> monkeyMethods,
               ChaosMonkeyRecorder recorder,
               BuildProducer<SyntheticBeanBuildItem> syntheticBeans) {

        List<ChaosMonkeyMethodMetadata> monkeyMetadata = monkeyMethods.stream()
                .map(m -> new ChaosMonkeyMethodMetadata(m.getClazz().simpleName(), m.getMethod().name()))
                .collect(Collectors.toList());

        syntheticBeans.produce(SyntheticBeanBuildItem.configure(ChaosMonkeyAnnotatedMethods.class).setRuntimeInit()
                .supplier(recorder.createContext(monkeyMetadata))
                .done());
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void includeRestEndpoints(BuildProducer<RouteBuildItem> routes,
                              BuildProducer<NotFoundPageDisplayableEndpointBuildItem> displayableEndpoints,
                              NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
                              ChaosMonkeyConfig chaosMonkeyConfig,
                              BodyHandlerBuildItem bodyHandlerBuildItem,
                              ChaosMonkeyRecorder recorder,
                              LaunchModeBuildItem launchMode,
                              ChaosMonkeyRuntimeConfig runtimeConfig) {

        if (shouldInclude(launchMode, chaosMonkeyConfig)) {
            Handler<RoutingContext> chaosMonkeyHandler = recorder.chaosMonkeyHandler();

            String basePath = nonApplicationRootPathBuildItem.adjustPath(chaosMonkeyConfig.basePath);

            routes.produce(new RouteBuildItem.Builder()
                    .routeFunction(recorder.routeFunction(basePath, bodyHandlerBuildItem.getHandler(), runtimeConfig))
                    .handler(chaosMonkeyHandler)
                    .build());

            displayableEndpoints.produce(new NotFoundPageDisplayableEndpointBuildItem(basePath + "/", "List configured Chaos-Monkeys"));
        }
    }

    private static boolean shouldInclude(LaunchModeBuildItem launchMode, ChaosMonkeyConfig chaosMonkeyConfig) {
        return launchMode.getLaunchMode().isDevOrTest() || chaosMonkeyConfig.alwaysInclude;
    }
}
