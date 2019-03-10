package net.fijma.mvc.example;

import net.fijma.mvc.AbstractController;
import net.fijma.mvc.AbstractView;
import net.fijma.mvc.Event;

public class AppController extends AbstractController<AppModel> {

    private AppController(AppModel model, AbstractView view, Event<Integer> key) {
        super(model, view, key);
    }

    private void onUpEvent(Void v) {
        model().inc();
    }

    private void onDownEvent(Void v) {
        model().dec();
    }

    public static AppController setup(AppModel model, Event<Integer> key) {

        // create views
        MainView view = new MainView(model);

        // wire model -> views
        model.onSomethingChanged.attach(view::somethingChanged);

        // create controller
        AppController controller = new AppController(model, view, key);

        // wire views -> controller
        view.down.attach(controller::onDownEvent);
        view.up.attach(controller::onUpEvent);

        return controller;
    }

}
