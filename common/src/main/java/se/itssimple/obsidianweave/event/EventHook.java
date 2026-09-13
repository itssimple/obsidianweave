package se.itssimple.obsidianweave.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A minimal listener list used by {@link ModEvents}. Listeners are invoked in registration order.
 * @param <T> The listener type.
 */
public final class EventHook<T> {
    /** Registered listeners. */
    private final List<T> listeners = new CopyOnWriteArrayList<>();

    /** Default constructor */
    public EventHook() {}

    /**
     * Registers a listener.
     * @param listener The listener.
     */
    public void register(T listener) {
        listeners.add(listener);
    }

    /**
     * Invokes every registered listener. Called by the platform modules when the underlying loader event fires.
     * @param invoker How to call one listener.
     */
    public void fire(Consumer<T> invoker) {
        for (T listener : listeners) {
            invoker.accept(listener);
        }
    }
}
