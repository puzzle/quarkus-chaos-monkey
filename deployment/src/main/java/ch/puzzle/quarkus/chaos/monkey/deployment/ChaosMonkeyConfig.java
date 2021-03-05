package ch.puzzle.quarkus.chaos.monkey.deployment;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "chaos-monkey", phase = ConfigPhase.BUILD_TIME)
public class ChaosMonkeyConfig {

    /**
     * The base path
     */
    @ConfigItem(defaultValue = "/chaos-monkey")
    String basePath;

    /**
     * Always include this. By default this will only be included in dev and test.
     * Setting this to true will also include this in Prod
     */
    @ConfigItem(defaultValue = "false")
    boolean alwaysInclude;
}
