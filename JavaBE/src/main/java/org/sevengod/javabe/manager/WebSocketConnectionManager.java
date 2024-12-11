package org.sevengod.javabe.manager;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WebSocketConnectionManager {
    private AtomicInteger currentConnections = new AtomicInteger(0);

    public int incrementConnections() {
        return currentConnections.incrementAndGet();
    }

    public int decrementConnections() {
        return currentConnections.decrementAndGet();
    }

    public int getCurrentConnections() {
        return currentConnections.get();
    }
}