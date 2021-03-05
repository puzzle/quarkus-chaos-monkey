package ch.puzzle.quarkus.chaos.monkey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Monkey {

    private static final Logger LOGGER = LoggerFactory.getLogger(Monkey.class);

    public String clazzName;
    public String methodName;
    public boolean enabled = false;
    public boolean throwException = false;
    public double errorRate = 0.0D;
    public long latencyMs = 0L;

    private final Random random = new Random();

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isThrowException() {
        return throwException;
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        if(enabled != null) {
            this.enabled = enabled;
        }
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Double errorRate) {
        if(errorRate != null) {
            this.errorRate = errorRate;
        }
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        if(latencyMs != null) {
            this.latencyMs = latencyMs;
        }
    }

    public void runErrorMonkey() {
        if (this.errorRate > 0.0D) {
            double strike = 1.0D - random.nextDouble();
            if (strike <= this.errorRate) {
                String msg = "ChaosMonkey strikes. ErrorRate=" + (this.errorRate * 100) + "%, CurrentStrike=" + String.format("%.2f", strike * 100);
                LOGGER.warn(msg);
                throw new IllegalStateException(msg);
            }
        }
    }

    public void runExceptionMonkey() {
        if (this.throwException) {
            throw new IllegalStateException("Monkey created an InternalServerError");
        }
    }

    public void runLatencyMonkey() {
        if (this.latencyMs > 0L) {
            LOGGER.warn("ChaosMonkey strikes. LatencyMs=" + (this.latencyMs));
            try {
                Thread.sleep(this.latencyMs);
            } catch (InterruptedException e) {
                /* ignore */
            }
        }
    }
}