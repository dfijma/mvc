package net.fijma.mvc.example;

import net.fijma.mvc.Event;

import java.util.logging.Logger;

public class AppModel {

    private static final Logger LOGGER = Logger.getLogger(AppModel.class.getName());

    final Event<Integer> onSomethingChanged = new Event<>();

    int value; // the actual model

    public void inc() {
        value++;
        LOGGER.info(() -> String.format("inc: value is now: %d", value));
        onSomethingChanged.trigger(value);
    }

    public void dec() {
        value--;
        LOGGER.info(() -> String.format("dec: value is now: %d", value));
        onSomethingChanged.trigger(value);
    }

}
