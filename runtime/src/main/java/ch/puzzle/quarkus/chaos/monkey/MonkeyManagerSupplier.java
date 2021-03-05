package ch.puzzle.quarkus.chaos.monkey;

import io.quarkus.arc.Arc;

import java.util.function.Supplier;

public class MonkeyManagerSupplier implements Supplier<MonkeyManager> {

    @Override
    public MonkeyManager get() {
        return Arc.container().instance(MonkeyManager.class).get();
    }
}
