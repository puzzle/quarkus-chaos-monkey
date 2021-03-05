package ch.puzzle.quarkus.chaos.monkey;

import java.util.Objects;

public class ChaosMonkeyMethodMetadata {
    public String clazzName;
    public String methodName;

    public ChaosMonkeyMethodMetadata() {

    }

    public ChaosMonkeyMethodMetadata(String clazzName, String methodName) {
        this.clazzName = clazzName;
        this.methodName = methodName;
    }

    public String getClazzName() {
        return clazzName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChaosMonkeyMethodMetadata that = (ChaosMonkeyMethodMetadata) o;
        return Objects.equals(clazzName, that.clazzName) &&
                methodName.equals(that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazzName, methodName);
    }
}
