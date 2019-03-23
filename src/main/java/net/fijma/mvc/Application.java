package net.fijma.mvc;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.*;

public abstract class Application {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    private final Map<Class<?>, ApplicationModule> modules = new HashMap<>();

    private volatile boolean run = true;
    private Queue<Msg> msgQueue = new ConcurrentLinkedQueue<>();

    protected Application() {
        configureLogger();
    }

    protected void registerModule(ApplicationModule m) {
        modules.put(m.getClass(), m);
    }

    public <T extends ApplicationModule> T getModule(Class<T> key) {
        return key.cast(modules.get(key));
    }

    private void configureLogger() {
        // get the global logger to configure it
        try {
            Logger logger = Logger.getLogger("");
            logger.removeHandler(logger.getHandlers()[0]);
            logger.setLevel(Level.INFO);
            Handler handler = new FileHandler("log.txt");
            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
            logger.addHandler(handler);
            LOGGER.info("log configured ");
        } catch (IOException e) {
            System.err.println("cannot configure logger: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run(Controller controller) throws IOException {

        for (ApplicationModule m : modules.values()) {
            LOGGER.info("starting: " + m.getClass().getSimpleName());
            m.start();
        }

        LOGGER.info("starting keyboard loop");
        // start reading the keyboard
        Thread kb = new Thread(this::keyboardLoop);
        kb.start();

        // run controller on main thread until it terminates
        controller.run();

        LOGGER.info("waiting for keyboard loop to stop");
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
            LOGGER.info("stopping: " + m.getClass().getSimpleName());
            m.stop();
        }

        LOGGER.info("run loop finished");
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
                LOGGER.warning("error reading keyboard: " + e.getMessage());
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
        if (!rc) LOGGER.severe(() -> "error offering msg: " + msg);
    }


}
