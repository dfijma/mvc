package net.fijma.mvc.example;

import net.fijma.mvc.Application;
import net.fijma.mvc.Controller;
import net.fijma.mvc.Msg;
import net.fijma.mvc.serial.Serial;

import java.io.IOException;
import java.util.logging.Logger;

public class AppController extends Controller<App, AppModel, MainView> {

    private static final Logger LOGGER = Logger.getLogger(AppController.class.getName());

    AppController(App application, AppModel model, MainView mainView) {
        super(application, model, mainView);

        // wire model -> views
        model.onSomethingChanged.attach(mainView::somethingChanged);

        // wire views -> controller
        mainView.down.attach(this::onDownEvent);
        mainView.up.attach(this::onUpEvent);
    }

    private void onUpEvent(Void v) {
        model.inc();
    }

    private void onDownEvent(Void v) {
        try {
            this.application.getModule(Serial.class).write("bla!");
        } catch (IOException e) {
            LOGGER.warning("cannot write serial port: " + e);
        }
        model.dec();
    }

    protected boolean onEvent(Msg msg) {
        if (msg instanceof Application.KeyMsg) {
            return mainView.key(((Application.KeyMsg)msg).key);
        } else if (msg instanceof Serial.SerialMsg) {
            return true; // ignore
        }
        return true; // catch-all, ignore and continue
    }

}
