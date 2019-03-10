package net.fijma.mvc;

public abstract class Controller<A extends Application, M, V extends View> {

    protected final M model;
    protected final V mainView;
    protected final A application;

    protected Controller(A application, M model, V mainView) {
        this.model = model;
        this.mainView = mainView;
        this.application = application;
    }

    void run() {
        mainView.clear();
        mainView.draw();
        boolean r = true;
        while (r) {
            try {
                Msg msg = application.poll();
                if (msg == null) {
                    Thread.sleep(10);
                    continue;
                }
                r = onEvent(msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected abstract boolean onEvent(Msg msg);

}
