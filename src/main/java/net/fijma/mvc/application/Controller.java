package net.fijma.mvc.application;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Controller {

    private final Model model;
    private final MainView view;

    private Controller(Model model, MainView view, Event<Integer> key) {
        this.model = model;
        this.view = view;
        key.attach(this::onKey);
    }

    public void run() {
        view.clear();
        view.draw();
        boolean r = true;
        while (r) {
            try {
                Msg msg = msgQueue.poll();
                if (msg == null) {
                    Thread.sleep(10);
                    continue;
                }
                if (msg instanceof KeyMsg) {
                    r = view.key(((KeyMsg)msg).key);
                }
            } catch (InterruptedException ignored) {  }
        }
    }

    private void onUpEvent(Void v) {
        model.inc();
    }

    private void onDownEvent(Void v) {
        model.dec();
    }

    public static Controller setup(Model model, Event<Integer> key) {

        // create views
        MainView view = new MainView(model);

        // wire model -> views
        model.onSomethingChanged.attach(view::somethingChanged);

        // create controller
        Controller controller = new Controller(model, view, key);

        // wire views -> controller
        view.down.attach(controller::onDownEvent);
        view.up.attach(controller::onUpEvent);

        return controller;
    }

    static class Msg {

    }

    static class KeyMsg extends Msg {
        final Integer key;
        KeyMsg(Integer key) { this.key = key; }
    }

    private Queue<Msg> msgQueue = new ConcurrentLinkedQueue<>();

    private void onKey(Integer key) {
        // post keypress to msg queue
        msgQueue.offer(new KeyMsg(key));
    }

}
