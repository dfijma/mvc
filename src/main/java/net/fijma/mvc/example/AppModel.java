package net.fijma.mvc.example;

import net.fijma.mvc.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppModel {

    private static final Logger log = LoggerFactory.getLogger(AppModel.class);

    final Event<Integer> onSomethingChanged = new Event<>();

    int value; // the actual model

    public void inc() {
        value++;
        log.info("inc: value is now: {}", value);
        onSomethingChanged.trigger(value);
    }

    public void dec() {
        value--;
        log.info("dec: value is now: {}", value);
        onSomethingChanged.trigger(value);
    }

}
