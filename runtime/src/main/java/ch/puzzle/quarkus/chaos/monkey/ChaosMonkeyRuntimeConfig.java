package ch.puzzle.quarkus.chaos.monkey;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "chaos-monkey", phase = ConfigPhase.RUN_TIME)
public class ChaosMonkeyRuntimeConfig {

    /**
     * If Chaos Monkey should be enabled. By default, Chaos Monkey is enabled if it is included (see
     * {@code always-include}).
     */
    @ConfigItem(name = "enable", defaultValue = "true")
    boolean enable;
}
