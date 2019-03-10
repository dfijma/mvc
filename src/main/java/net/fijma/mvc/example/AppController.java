package net.fijma.mvc.example;

import net.fijma.mvc.Application;
import net.fijma.mvc.Controller;
import net.fijma.mvc.Msg;
import net.fijma.mvc.serial.Serial;

public class AppController extends Controller<App, AppModel, MainView> {

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
