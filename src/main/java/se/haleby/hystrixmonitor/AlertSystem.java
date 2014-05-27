package se.haleby.hystrixmonitor;

import org.springframework.stereotype.Service;

@Service
public class AlertSystem {

    private boolean isCircuitBreakerClosed;

    public AlertSystem() {
        this.isCircuitBreakerClosed = true;
    }

    public void reportCircuitBreakerOpened() {
        this.isCircuitBreakerClosed = false;
    }

    public void reportCircuitBreakerClosed() {
        this.isCircuitBreakerClosed = true;
    }


    public boolean isCircuitBreakerClosed() {
        return isCircuitBreakerClosed;
    }

    public boolean isCircuitBreakerOpened() {
        return !isCircuitBreakerClosed();
    }
}
