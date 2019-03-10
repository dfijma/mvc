package net.fijma.mvc;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractController<M extends Model> {

    private final M model;
    private final AbstractView view;

    protected AbstractController(M model, AbstractView mainView, Event<Integer> key) {
        this.model = model;
        this.view = mainView;
        key.attach(this::onKey);
    }

    protected M model() { return model; }

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
            } catch (InterruptedException ignored) { ; }
        }
    }

    static class Msg { }

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
