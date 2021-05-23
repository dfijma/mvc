package net.fijma.mvc;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Event<T> {

    private final Set<Consumer<T>> listeners = new HashSet<>();

    public void attach(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void detach(Consumer<T> listener) { listeners.remove(listener); }

    public void trigger(T arg) {
        for (Consumer<T> listener : listeners) {
            listener.accept(arg);
        }
    }
}
