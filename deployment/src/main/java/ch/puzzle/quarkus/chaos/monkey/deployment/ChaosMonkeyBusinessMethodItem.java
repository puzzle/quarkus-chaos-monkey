package ch.puzzle.quarkus.chaos.monkey.deployment;

import io.quarkus.builder.item.MultiBuildItem;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;

public final class ChaosMonkeyBusinessMethodItem extends MultiBuildItem {

    private final ClassInfo clazz;
    private final MethodInfo method;

    public ChaosMonkeyBusinessMethodItem(ClassInfo clazz, MethodInfo method) {
        this.clazz = clazz;
        this.method = method;
    }

    public ClassInfo getClazz() {
        return clazz;
    }

    public MethodInfo getMethod() {
        return method;
    }

}
