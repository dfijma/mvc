package net.fijma.mvc.application;

import java.util.logging.Logger;

public class Model {

    private Logger log = Logger.getGlobal();

    final Event<Integer> onSomethingChanged = new Event<>();

    int value; // the actual model

    public void inc() {
        value++;
        log.info(() -> String.format("inc: value is now: %d", value));
        onSomethingChanged.trigger(value);

    }

    public void dec() {
        value--;
        log.info(() -> String.format("dec: value is now: %d", value));
        onSomethingChanged.trigger(value);
    }

}
