package net.fijma.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Map<Class<?>, ApplicationModule> modules = new HashMap<>();

    private volatile boolean run = true;
    private final Queue<Msg> msgQueue = new ConcurrentLinkedQueue<>();

    protected Application() { }

    protected void registerModule(ApplicationModule m) {
        modules.put(m.getClass(), m);
    }

    public <T extends ApplicationModule> T getModule(Class<T> key) {
        return key.cast(modules.get(key));
    }


    public void run(Controller controller) throws IOException {

        for (ApplicationModule m : modules.values()) {
            log.info("starting: {}" , m.getClass().getSimpleName());
            m.start();
        }

        log.info("starting keyboard loop");
        // start reading the keyboard
        Thread kb = new Thread(this::keyboardLoop);
        kb.start();

        // run controller on main thread until it terminates
        controller.run();

        log.info("waiting for keyboard loop to stop");
        // wait for keyboard loop to stop
        run = false;
        while (true) {
            try {
                kb.join();
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (ApplicationModule m : modules.values()) {
            log.info("stopping: {}", m.getClass().getSimpleName());
            m.stop();
        }

        log.info("run loop finished");
    }

    // http://develorium.com/2016/03/unbuffered-standard-input-in-java-console-applications/
    private void keyboardLoop() {
        Console console = System.console();
        while (run) {
            try {
                boolean a = console.reader().ready();
                if (!a) {
                    Thread.sleep(10);
                    continue;
                }
                offer(new KeyMsg(console.reader().read()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                log.warn("error reading keyboard: {}", e.getMessage());
            }
        }
    }

    static public class KeyMsg implements Msg {
        public final Integer key;
        private KeyMsg(Integer key) { this.key = key; }
    }

    Msg poll() {
        return msgQueue.poll();
    }

    public void offer(Msg msg) {
        boolean rc = msgQueue.offer(msg);
        if (!rc) {
            log.error("error offering msg: {}", msg);
        }
    }


}
