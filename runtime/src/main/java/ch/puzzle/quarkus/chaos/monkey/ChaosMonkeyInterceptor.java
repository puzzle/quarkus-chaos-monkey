package ch.puzzle.quarkus.chaos.monkey;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@ChaosMonkey
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class ChaosMonkeyInterceptor {

    @Inject
    MonkeyManager monkeyService;

    @AroundInvoke
    public Object monkey(InvocationContext context) throws Exception {
        Monkey monkey = monkeyService.get(context.getMethod().getDeclaringClass(), context.getMethod());
        Object ret = null;
        if (monkey.isEnabled()) {
            ChaosMonkey monkeyAnnotation = context.getMethod().getAnnotation(ChaosMonkey.class);
            // before invocation
            ret = context.proceed();
            // after invocation

            if (monkeyAnnotation.latency()) {
                monkey.runLatencyMonkey();
            }

            if (monkeyAnnotation.errorRate()) {
                monkey.runErrorMonkey();
            }

            if (monkeyAnnotation.exception()) {
                monkey.runExceptionMonkey();
            }

            return ret;
        }

        return context.proceed();
    }
}
