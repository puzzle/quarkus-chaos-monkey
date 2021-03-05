package ch.puzzle.quarkus.chaos.monkey;

import java.util.List;

public interface ChaosMonkeyAnnotatedMethods {

    List<ChaosMonkeyMethodMetadata> getChaosMonkeyMethods();
}
