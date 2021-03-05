package ch.puzzle.quarkus.chaos.monkey;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MonkeyManager {

    private static final String DELIMITER = "-";

    public Map<String, Monkey> monkeys = new HashMap<>();

    @Inject
    ChaosMonkeyAnnotatedMethods context;

    public Monkey get(Class<?> clazz, Method method) {
        return this.get(this.toCallerId(clazz, method));
    }

    public Monkey get(String clazzName, String methodName) {
        return this.get(this.toCallerId(clazzName, methodName));
    }

    public Monkey get(String callerId) {
        if (callerId != null) {
            if (monkeys.containsKey(callerId)) {
                return monkeys.get(callerId);
            } else if (callerId.contains(DELIMITER)) {
                String clazz = callerId.split(DELIMITER)[0];
                if (monkeys.containsKey(clazz)) {
                    return monkeys.get(clazz);
                }
            }
        }

        return new Monkey();
    }

    public void add(Monkey monkey) {
        this.addMonkey(monkey, this.toCallerId(monkey.getClazzName(), monkey.getMethodName()));
    }

    void addMonkey(Monkey monkey, String callerId) {
        if (monkey == null || callerId == null) {
            return;
        }

        this.monkeys.put(callerId, monkey);
    }

    public void removeMonkey(String clazzName, String methodName) {
        this.removeMonkey(this.toCallerId(clazzName, methodName));
    }

    public void removeMonkey(String callerId) {
        if (callerId != null && this.monkeys.containsKey(callerId)) {
            this.monkeys.remove(callerId);
        } else {
            throw new IllegalStateException("Monkey '" + callerId + "' not found");
        }
    }


    String toCallerId(Class<?> clazz, Method method) {
        if (clazz == null) {
            return null;
        }

        if (method == null) {
            return toCallerId(clazz.getSimpleName(), null);
        }

        return toCallerId(clazz.getSimpleName(), method.getName());
    }

    String toCallerId(String clazzName, String methodName) {
        if (clazzName == null && methodName == null) {
            return null;
        }

        String cn = clazzName != null ? clazzName.toLowerCase().replace("-", "") : null;
        String mn = methodName != null ? methodName.toLowerCase().replace("-", "") : null;

        if (mn == null || mn.isEmpty()) {
            return cn;
        } else {
            return cn + DELIMITER + mn;
        }
    }

    public Map<String, Monkey> getAll() {
        return new HashMap<>(monkeys);
    }

    public List<Monkey> getMonkeys() {
        return new ArrayList<>(this.monkeys.values());
    }

    public List<ChaosMonkeyMethodMetadata> getKnownMonkeys() {
        return this.context.getChaosMonkeyMethods();
    }

    public long getKnownMonkeySize() {
        return this.context.getChaosMonkeyMethods().size();
    }

    public long getSize() {
        return this.monkeys.size();
    }

    public long getActive() {
        return this.monkeys.values().stream().filter(m -> m.enabled).count();
    }

}
